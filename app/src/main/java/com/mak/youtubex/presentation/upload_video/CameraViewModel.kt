package com.mak.youtubex.presentation.upload_video

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresPermission
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    app: Application
) : AndroidViewModel(app) {

    /* ----------------------------- Recording State ----------------------------- */

    sealed interface RecordingState {
        object Idle : RecordingState
        data class Active(
            val recording: Recording,
            val isPaused: Boolean
        ) : RecordingState
    }

    private val _recordingState =
        MutableStateFlow<RecordingState>(RecordingState.Idle)
    val recordingState: StateFlow<RecordingState> =
        _recordingState.asStateFlow()

    /* ----------------------------- Camera State ----------------------------- */

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()

    private val _camera = MutableStateFlow<Camera?>(null)
    val camera: StateFlow<Camera?> = _camera.asStateFlow()

    private val _videoCapture = MutableStateFlow<VideoCapture<Recorder>?>(null)
    val videoCapture: StateFlow<VideoCapture<Recorder>?> = _videoCapture.asStateFlow()

    private val _videoRecordingUri = MutableStateFlow<Uri?>(null)
    val videoRecordingUri: StateFlow<Uri?> = _videoRecordingUri.asStateFlow()

    private val _lensFacing = MutableStateFlow(CameraSelector.LENS_FACING_BACK)
    val lensFacing: StateFlow<Int> = _lensFacing.asStateFlow()

    private val _flashMode = MutableStateFlow(ImageCapture.FLASH_MODE_OFF)
    val flashMode: StateFlow<Int> = _flashMode.asStateFlow()

    private var currentLifecycleOwner: LifecycleOwner? = null

    private val appContext = app.applicationContext
    private val mainExecutor: Executor
        get() = ContextCompat.getMainExecutor(appContext)

    private suspend fun provider(): ProcessCameraProvider =
        ProcessCameraProvider.awaitInstance(getApplication())

    /* ----------------------------- Binding ----------------------------- */

    fun bindCapture(
        lifecycleOwner: LifecycleOwner,
        quality: Quality = Quality.FHD
    ) {
        currentLifecycleOwner = lifecycleOwner

        viewModelScope.launch {
            val provider = provider()

            val selector = CameraSelector.Builder()
                .requireLensFacing(_lensFacing.value)
                .build()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider { req ->
                    _surfaceRequest.value = req
                }
            }

            val recorder = Recorder.Builder()
                .setQualitySelector(
                    QualitySelector.from(
                        quality,
                        FallbackStrategy.higherQualityOrLowerThan(Quality.FHD)
                    )
                )
                .build()

            val videoCapture = VideoCapture.withOutput(recorder)

            try {
                provider.unbindAll()

                _camera.value = provider.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    videoCapture
                )

                _videoCapture.value = videoCapture

            } catch (_: Exception) {
                // optional logging
            }
        }
    }

    /* ----------------------------- Camera Controls ----------------------------- */

    fun toggleCamera() {
        _lensFacing.update {
            if (it == CameraSelector.LENS_FACING_BACK)
                CameraSelector.LENS_FACING_FRONT
            else
                CameraSelector.LENS_FACING_BACK
        }

        currentLifecycleOwner?.let { bindCapture(it) }
    }

    fun toggleFlash() {
        _flashMode.update {
            when (it) {
                ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                else -> ImageCapture.FLASH_MODE_OFF
            }
        }

        _camera.value?.cameraControl
            ?.enableTorch(_flashMode.value == ImageCapture.FLASH_MODE_ON)
    }

    /* ----------------------------- Recording Logic ----------------------------- */

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun toggleRecording() {
        val vc = _videoCapture.value ?: return

        when (val state = _recordingState.value) {

            RecordingState.Idle -> {
                startRecording(vc)
            }

            is RecordingState.Active -> {
                if (state.isPaused) {
                    state.recording.resume()
                    _recordingState.value =
                        state.copy(isPaused = false)
                } else {
                    state.recording.pause()
                    _recordingState.value =
                        state.copy(isPaused = true)
                }
            }
        }
    }

    fun stopRecording() {
        val state = _recordingState.value
        if (state is RecordingState.Active) {
            state.recording.stop()
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording(
        videoCapture: VideoCapture<Recorder>
    ) {
        val name = "VID_${System.currentTimeMillis()}.mp4"

        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Video.Media.RELATIVE_PATH,
                    "DCIM/YoutubeX"
                )
            }
        }

        val outputOptions = MediaStoreOutputOptions.Builder(
            appContext.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(values)
            .build()

        val recording = videoCapture.output
            .prepareRecording(appContext, outputOptions)
            .withAudioEnabled()
            .start(mainExecutor) { event ->

                if (event is VideoRecordEvent.Finalize) {

                    if (!event.hasError()) {
                        _videoRecordingUri.value =
                            event.outputResults.outputUri
                    }

                    _recordingState.value = RecordingState.Idle
                }
            }

        _recordingState.value =
            RecordingState.Active(
                recording = recording,
                isPaused = false
            )
    }

    /* ----------------------------- Helpers ----------------------------- */

    fun clearVideoUri() {
        _videoRecordingUri.value = null
    }

    fun releaseCamera() {
        viewModelScope.launch {
            try {
                stopRecording()

                val provider = provider()
                provider.unbindAll()

                _camera.value = null
                _videoCapture.value = null
                _surfaceRequest.value = null
                currentLifecycleOwner = null

                _recordingState.value = RecordingState.Idle

            } catch (_: Exception) {
                // optional logging
            }
        }
    }
}
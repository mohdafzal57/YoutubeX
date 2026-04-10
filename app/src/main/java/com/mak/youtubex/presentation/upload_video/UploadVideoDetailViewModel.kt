package com.mak.youtubex.presentation.upload_video

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.youtubex.domain.model.VideoUploadRequest
import com.mak.youtubex.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadVideoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val uploadVideoRepository: VideoRepository
) : ViewModel() {

    private val videoUri: Uri? =
        savedStateHandle.get<String>("videoUri")
            ?.let { Uri.decode(it).toUri() }

    private val _uiState =
        MutableStateFlow(VideoDetailsUiState(videoUri = videoUri))
    val uiState: StateFlow<VideoDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UploadEvent>()
    val events: SharedFlow<UploadEvent> = _events.asSharedFlow()

    fun onThumbnailChange(uri: Uri) {
        _uiState.update { it.copy(thumbnailUri = uri) }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onUpload() {
        val currentUri = _uiState.value.videoUri ?: return
        val currentThumbnail = _uiState.value.thumbnailUri ?: return

        viewModelScope.launch {
            val request = VideoUploadRequest(
                videoFile = currentUri,
                thumbnail = currentThumbnail,
                title = _uiState.value.title,
                description = _uiState.value.description
            )
            _events.emit(UploadEvent.NavigateHome)
            uploadVideoRepository.uploadVideo(request)
        }
    }
}

data class VideoDetailsUiState(
    val title: String = "",
    val description: String = "",
    val thumbnailUri: Uri? = null,
    val videoUri: Uri? = null,
)

sealed class UploadEvent {
    object NavigateHome : UploadEvent()
}

/***Generate Multiple Frames
 * fun generateThumbnails(context: Context) {
 *     val uri = videoUri ?: return
 *
 *     viewModelScope.launch(Dispatchers.IO) {
 *         try {
 *             val retriever = MediaMetadataRetriever()
 *             retriever.setDataSource(context, uri)
 *
 *             val durationMs = retriever
 *                 .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
 *                 ?.toLongOrNull() ?: 0L
 *
 *             if (durationMs <= 0) return@launch
 *
 *             val frames = mutableListOf<Bitmap>()
 *
 *             val percentages = listOf(0.1, 0.3, 0.5, 0.7, 0.9)
 *
 *             for (p in percentages) {
 *                 val timeUs = (durationMs * p * 1000).toLong()
 *
 *                 val bitmap = retriever.getFrameAtTime(
 *                     timeUs,
 *                     MediaMetadataRetriever.OPTION_CLOSEST_SYNC
 *                 )
 *
 *                 bitmap?.let { frames.add(it) }
 *             }
 *
 *             retriever.release()
 *
 *             withContext(Dispatchers.Main) {
 *                 _uiState.update {
 *                     it.copy(thumbnailOptions = frames)
 *                 }
 *             }
 *
 *         } catch (e: Exception) {
 *             e.printStackTrace()
 *         }
 *     }
 * }
 */
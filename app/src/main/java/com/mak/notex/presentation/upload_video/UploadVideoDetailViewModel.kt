package com.mak.notex.presentation.upload_video

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.notex.domain.model.VideoUploadRequest
import com.mak.notex.domain.repository.VideoRepository
import com.mak.notex.core.data.util.onFailure
import com.mak.notex.core.data.util.onSuccess
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
            _uiState.update { it.copy(isLoading = true, error = null) }

            val request = VideoUploadRequest(
                videoFile = currentUri,
                thumbnail = currentThumbnail,
                title = _uiState.value.title,
                description = _uiState.value.description
            )
            uploadVideoRepository.uploadVideo(request)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(UploadEvent.NavigateHome)
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(UploadEvent.ShowError(it.toString()))
                }
        }
    }
}

data class VideoDetailsUiState(
    val title: String = "",
    val description: String = "",
    val thumbnailUri: Uri? = null,
    val videoUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class UploadEvent {
    object NavigateHome : UploadEvent()
    data class ShowError(val message: String) : UploadEvent()
}

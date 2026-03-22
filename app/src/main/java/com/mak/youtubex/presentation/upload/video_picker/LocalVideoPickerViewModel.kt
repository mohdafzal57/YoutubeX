package com.mak.youtubex.presentation.upload.video_picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.youtubex.domain.model.LocalVideo
import com.mak.youtubex.domain.model.VideoFolder
import com.mak.youtubex.domain.repository.LocalMediaRepository
import com.mak.youtubex.utils.MediaPrefetcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalVideoPickerViewModel @Inject constructor(
    private val localMediaRepository: LocalMediaRepository,
    private val mediaPrefetcher: MediaPrefetcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoPickerUiState())
    val uiState: StateFlow<VideoPickerUiState> = _uiState.asStateFlow()

    init {
        observeFolders()
        observeVideos()
    }

    private fun observeFolders() {
        viewModelScope.launch {
            localMediaRepository.getVideoFolders().collect { folders ->
                _uiState.update { it.copy(videoFolders = folders) }
            }
        }
    }

    private fun observeVideos(folderId: Long? = null) {
        viewModelScope.launch {
            localMediaRepository.getLocalVideos(folderId)
                .distinctUntilChanged()
                .collect { videos ->
                    _uiState.update { it.copy(localVideos = videos) }
                    mediaPrefetcher.prefetchVideoThumbnails(videos)
                }
        }
    }

    fun selectFolder(folder: VideoFolder?) {
        _uiState.update { it.copy(selectedFolder = folder) }
        observeVideos(folder?.id)
    }
}

data class VideoPickerUiState(
    val localVideos: List<LocalVideo> = emptyList(),
    val selectedFolder: VideoFolder? = null,
    val videoFolders: List<VideoFolder> = emptyList()
)
package com.mak.notex.presentation.upload_video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.notex.presentation.upload_video.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    fun onActionSelected(mode: ContentCreationMode) {
        _uiState.update {
            it.copy(
                contentMode = mode,
                isBottomSheetExpanded = false
            )
        }
    }

    fun toggleBottomSheet() {
        _uiState.update {
            it.copy(isBottomSheetExpanded = !it.isBottomSheetExpanded)
        }
    }

    fun closeBottomSheet() {
        _uiState.update {
            it.copy(isBottomSheetExpanded = false)
        }
    }

    fun startCameraPreview() {
        _uiState.update {
            it.copy(
                cameraState = it.cameraState.copy(isPreviewActive = true)
            )
        }
    }

    fun stopCameraPreview() {
        _uiState.update {
            it.copy(
                cameraState = CameraState()
            )
        }
    }

    fun startRecording() {
        _uiState.update {
            it.copy(
                cameraState = it.cameraState.copy(isRecording = true)
            )
        }
    }

    fun stopRecording() {
        _uiState.update {
            it.copy(
                cameraState = it.cameraState.copy(isRecording = false, recordingDuration = 0L)
            )
        }
    }

    fun updatePostText(text: String) {
        _uiState.update {
            it.copy(
                postState = it.postState.copy(text = text)
            )
        }
    }

    fun updatePostVisibility(visibility: PostVisibility) {
        _uiState.update {
            it.copy(
                postState = it.postState.copy(visibility = visibility)
            )
        }
    }

    fun submitPost() {
        viewModelScope.launch {
            // Handle post submission logic here
            val post = _uiState.value.postState
            // TODO: Submit to repository

            // Reset after submission
            _uiState.update {
                it.copy(
                    postState = PostState(),
                    contentMode = ContentCreationMode.Short
                )
            }
        }
    }

    fun closeContent() {
        _uiState.update {
            it.copy(
                contentMode = ContentCreationMode.Short,
                cameraState = CameraState(),
                postState = PostState()
            )
        }
    }
}
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
}

data class MainUIState(
    val contentMode: ContentCreationMode = ContentCreationMode.Short,
    val postState: PostState = PostState(),
    val isBottomSheetExpanded: Boolean = false
)

sealed class ContentCreationMode(val title: String) {

    object Video : ContentCreationMode("video")
    object Short : ContentCreationMode("short")
    object Post : ContentCreationMode("post")

    companion object {
        val allModes get() = listOf(Video, Short, Post)
    }
}

data class PostState(
    val text: String = "",
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val hasMedia: Boolean = false
)

enum class PostVisibility {
    PUBLIC, PRIVATE, FOLLOWERS
}

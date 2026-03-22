package com.mak.youtubex.presentation.upload.create_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    fun updateText(text: String) {
        _uiState.update { it.copy(text = text) }
    }

    fun updateVisibility(visibility: PostVisibility) {
        _uiState.update { it.copy(visibility = visibility) }
    }

    fun attachMedia() {
        _uiState.update { it.copy(hasMedia = true) }
    }

    fun submitPost() {
        viewModelScope.launch {
            // call repository here

            _uiState.value = PostUiState()
        }
    }
}

data class PostUiState(
    val text: String = "",
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val hasMedia: Boolean = false
)

enum class PostVisibility {
    PUBLIC, PRIVATE, FOLLOWERS
}

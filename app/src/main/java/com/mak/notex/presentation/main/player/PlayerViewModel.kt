package com.mak.notex.presentation.main.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.notex.domain.repository.LikeRepository
import com.mak.notex.core.data.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val likeRepository: LikeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    private val _videoId = savedStateHandle.getStateFlow("videoId", "")

    fun onEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.ToggleLike -> handleToggleLike()
            is PlayerEvent.OpenCommentSheet -> handleOpenCommentSheet()
            is PlayerEvent.CloseCommentSheet -> handleCloseCommentSheet()
            is PlayerEvent.SubmitComment -> handleSubmitComment(event.text)
            is PlayerEvent.Share -> Unit
        }
    }

    private fun handleToggleLike() {
        viewModelScope.launch {
            likeRepository.toggleVideoLike(_videoId.value)
                .onSuccess { isLiked ->
                    _uiState.update { state ->
                        state.copy(isLiked = false)
                    }
                }
        }
//        _uiState.update { state ->
//            val newIsLiked = !state.isLiked
//            val newLikeCount = if (newIsLiked) {
//                state.likeCount + 1
//            } else {
//                state.likeCount - 1
//            }
//            state.copy(
//                isLiked = newIsLiked,
//                likeCount = newLikeCount
//            )
//        }
    }

    private fun handleOpenCommentSheet() {
        _uiState.update { state ->
            state.copy(isCommentSheetVisible = true)
        }
    }

    private fun handleCloseCommentSheet() {
        _uiState.update { state ->
            state.copy(isCommentSheetVisible = false)
        }
    }

    private fun handleSubmitComment(text: String) {
        if (text.isBlank()) return
        _uiState.update { state ->
            state.copy(
                commentCount = state.commentCount + 1,
                isCommentSheetVisible = false
            )
        }
    }
}

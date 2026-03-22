package com.mak.youtubex.presentation.main.player

data class PlayerUiState(
    val isLiked: Boolean = false,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val creatorName: String = "",
    val title: String = "",
    val isCommentSheetVisible: Boolean = false
)

sealed interface PlayerEvent {
    data object ToggleLike : PlayerEvent
    data object OpenCommentSheet : PlayerEvent
    data object CloseCommentSheet : PlayerEvent
    data class SubmitComment(val text: String) : PlayerEvent
    data object Share : PlayerEvent
}
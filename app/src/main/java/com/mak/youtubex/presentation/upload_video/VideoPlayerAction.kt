package com.mak.youtubex.presentation.upload_video

sealed class VideoPlayerAction {
    data object ToggleControls : VideoPlayerAction()
    data class PlayPause(val play: Boolean) : VideoPlayerAction()
    data class Seek(val position: Long) : VideoPlayerAction()
    data class Progress(val position: Long, val duration: Long) : VideoPlayerAction()
    data object HideControls : VideoPlayerAction()
    data class Mute(val mute: Boolean) : VideoPlayerAction()
}
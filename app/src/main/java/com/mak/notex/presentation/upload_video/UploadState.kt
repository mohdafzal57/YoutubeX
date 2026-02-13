package com.mak.notex.presentation.upload_video

sealed class ContentCreationMode {
    object Video : ContentCreationMode()
    object Short : ContentCreationMode()
    object Post : ContentCreationMode()
}

data class CameraState(
    val isPreviewActive: Boolean = false,
    val isRecording: Boolean = false,
    val recordingDuration: Long = 0L
)

data class PostState(
    val text: String = "",
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val hasMedia: Boolean = false
)

enum class PostVisibility {
    PUBLIC, PRIVATE, FOLLOWERS
}

data class MainUIState(
    val contentMode: ContentCreationMode = ContentCreationMode.Short,
    val cameraState: CameraState = CameraState(),
    val postState: PostState = PostState(),
    val isBottomSheetExpanded: Boolean = false
)
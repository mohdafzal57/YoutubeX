package com.mak.notex.presentation.upload

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mak.notex.presentation.upload_video.ContentCreationMode
import com.mak.notex.presentation.upload_video.UploadViewModel

@Composable
fun VideoScreen(
    viewModel: UploadViewModel = hiltViewModel(),
    onCloseClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreatePostContent(
        postState = uiState.postState,
        onTextChange = viewModel::updatePostText,
        onVisibilityChange = viewModel::updatePostVisibility,
        onPostClick = viewModel::submitPost,
        onCloseClick = onCloseClick,
        modifier = Modifier.fillMaxSize()
    )
}

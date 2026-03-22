package com.mak.youtubex.presentation.main.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    videoUrl: String,
    uiState: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    Box(modifier = Modifier.fillMaxSize()) {
        VideoPlayerBackground(videoUrl = videoUrl)

        TransparentTopBar(onBack = onBackClick)

//        ActionButtonsColumn(
//            uiState = uiState,
//            onEvent = onEvent,
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .padding(end = 16.dp)
//        )
//
//        VideoInfoSection(
//            creatorName = uiState.creatorName,
//            title = uiState.title,
//            modifier = Modifier
//                .align(Alignment.BottomStart)
//                .padding(start = 16.dp, bottom = 24.dp, end = 80.dp)
//        )

        if (uiState.isCommentSheetVisible) {
            CommentBottomSheet(
                sheetState = sheetState,
                onDismiss = { onEvent(PlayerEvent.CloseCommentSheet) },
                onSubmit = { text -> onEvent(PlayerEvent.SubmitComment(text)) }
            )
        }
    }
}

@Composable
private fun VideoPlayerBackground(videoUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VideoPlayer(videoUrl = videoUrl)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransparentTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun ActionButtonsColumn(
    uiState: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        LikeActionButton(
            isLiked = uiState.isLiked,
            likeCount = uiState.likeCount,
            onClick = { onEvent(PlayerEvent.ToggleLike) }
        )

        CommentActionButton(
            commentCount = uiState.commentCount,
            onClick = { onEvent(PlayerEvent.OpenCommentSheet) }
        )

        ShareActionButton(
            onClick = { onEvent(PlayerEvent.Share) }
        )
    }
}

@Composable
private fun LikeActionButton(
    isLiked: Boolean,
    likeCount: Int,
    onClick: () -> Unit
) {
    ActionButton(
        icon = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
        count = likeCount,
        contentDescription = "Like",
        tint = if (isLiked) Color.Red else Color.White,
        onClick = onClick
    )
}

@Composable
private fun CommentActionButton(
    commentCount: Int,
    onClick: () -> Unit
) {
    ActionButton(
        icon = Icons.Outlined.ModeComment,
        count = commentCount,
        contentDescription = "Comment",
        tint = Color.White,
        onClick = onClick
    )
}

@Composable
private fun ShareActionButton(onClick: () -> Unit) {
    ActionButton(
        icon = Icons.Outlined.Share,
        count = null,
        contentDescription = "Share",
        tint = Color.White,
        onClick = onClick
    )
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    count: Int?,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(32.dp)
            )
        }
        count?.let { value ->
            Text(
                text = formatCount(value),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun VideoInfoSection(
    creatorName: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = creatorName,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}
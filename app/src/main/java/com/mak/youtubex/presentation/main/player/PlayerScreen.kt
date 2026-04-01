package com.mak.youtubex.presentation.main.player

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    videoUrl: String,
    uiState: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit,
    onBackClick: () -> Unit,
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val showControls by viewModel.showControls.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val totalDuration by viewModel.totalDuration.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Video Background
        VideoPlayer(
            videoUrl = videoUrl,
            modifier = Modifier.fillMaxSize(),
            isPlaying = isPlaying,
            showControls = showControls,
            currentPosition = currentPosition,
            totalDuration = totalDuration,
            onAction = viewModel::onAction
        )

        // 2. Bottom gradient scrim — text legibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)) //was 0.65f
                    )
                )
        )

        // 3. Top Bar
        TransparentTopBar(onBack = onBackClick)

        // 4. Bottom Overlay
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 12.dp, vertical = 16.dp)
                .padding(bottom = 100.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VideoInfoSection(
                creatorName = uiState.creatorName,
                title = uiState.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            ActionButtonsColumn(
                uiState = uiState,
                onEvent = onEvent
            )
        }

        // 5. Bottom Sheet
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
private fun ActionButtonsColumn(
    uiState: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // was 20.dp
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
private fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    count: Int?,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp) // constrain touch target explicitly
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = modifier.size(26.dp) // was 32.dp
            )
        }
        count?.let {
            Text(
                text = formatCount(it),
                color = Color.White,
                fontSize = 11.sp,                        // explicit, smaller than labelSmall default
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun LikeActionButton(
    isLiked: Boolean,
    likeCount: Int,
    onClick: () -> Unit
) {
    // Spring pop on like
    var trigger by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (trigger) 1.35f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { trigger = false },
        label = "like_scale"
    )
    ActionButton(
        icon = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
        count = likeCount,
        contentDescription = "Like",
        tint = if (isLiked) Color.Red else Color.White,
        modifier = Modifier.scale(scale),
        onClick = { trigger = true; onClick() }
    )
}

@Composable
private fun CommentActionButton(commentCount: Int, onClick: () -> Unit) {
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
private fun VideoInfoSection(
    creatorName: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        /*Text(
            text = "@$creatorName",  // TikTok-style prefix
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )*/
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

//fun formatCount(count: Int): String = when {
//    count >= 1_000_000 -> "%.1fM".format(count / 1_000_000f)
//    count >= 1_000     -> "%.1fK".format(count / 1_000f)
//    else               -> count.toString()
//}
private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}
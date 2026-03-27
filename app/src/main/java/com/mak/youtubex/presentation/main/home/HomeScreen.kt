package com.mak.youtubex.presentation.main.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.mak.youtubex.presentation.main.common.BottomLoader
import com.mak.youtubex.presentation.main.common.ErrorScreen
import com.mak.youtubex.presentation.main.common.RetryFooter
import com.mak.youtubex.presentation.main.common.VideoItem
import com.mak.youtubex.presentation.main.common.YTPullToRefreshBox
import com.mak.youtubex.presentation.main.common.YTTopAppBar

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(
//    onPlayVideo: (String, String) -> Unit,
//    onNavigateToChannel: (String, String) -> Unit,
//    onNavigateToSearch: () -> Unit,
//    viewModel: HomeViewModel = hiltViewModel()
//) {
//    val videos = viewModel.videos.collectAsLazyPagingItems()
//    val listState = rememberLazyListState()
//
//    val isRefreshing = videos.loadState.refresh is LoadState.Loading && videos.itemCount > 0
//
////    LaunchedEffect(videos.loadState.refresh) {
////        if (videos.loadState.refresh is LoadState.NotLoading && videos.itemCount > 0 ) {
////            listState.animateScrollToItem(0)
////        }
////    }
//
//    Scaffold(
//        topBar = { YTTopAppBar(
//            onNavigateToSearch = onNavigateToSearch
//        ) }
//    ) { innerPadding ->
//
//        YTPullToRefreshIndicator(
//            isRefreshing = isRefreshing,
//            onRefresh = { videos.refresh() },
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .background(MaterialTheme.colorScheme.background)
//        ) {
//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                state = listState
//            ) {
//
//                items(
//                    count = videos.itemCount,
//                    key = videos.itemKey { it.id }
//                ) { index ->
//                    videos[index]?.let { video ->
//                        VideoItem(
//                            video = video,
//                            onClick = { onPlayVideo(video.videoFile, video.id) },
//                            onNavigateToChannel = {
//                                onNavigateToChannel(
//                                    video.username,
//                                    video.ownerId
//                                )
//                            }
//                        )
//                    }
//                }
//
//                // Append loading state (at the end of the list)
//                when (videos.loadState.append) {
//                    is LoadState.Loading -> {
//                        item { BottomLoader() }
//                    }
//
//                    is LoadState.Error -> {
//                        item {
//                            RetryFooter(
//                                onRetry = { videos.retry() }
//                            )
//                        }
//                    }
//
//                    else -> {}
//                }
//            }
//
//            // Refresh state (initial load or manual refresh)
//            when (videos.loadState.refresh) {
//                is LoadState.Loading -> {
//                    if (videos.itemCount == 0) {
//                        FullScreenLoader()
//                    }
//                }
//
//                is LoadState.Error -> {
//                    if (videos.itemCount == 0) {
//                        ErrorScreen(
//                            message = "Failed to load videos",
//                            onRetry = { videos.retry() }
//                        )
//                    }
//                }
//
//                else -> {}
//            }
//        }
//    }
//
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPlayVideo: (String, String) -> Unit,
    onNavigateToChannel: (String, String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val videos = viewModel.videos.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    // 1. Better Refresh State handling
    val isRefreshing = videos.loadState.refresh is LoadState.Loading

    // 2. Scroll to top on refresh completion
    LaunchedEffect(videos.loadState.refresh) {
        if (videos.loadState.refresh is LoadState.NotLoading && videos.itemCount > 0) {
            // Only scroll if we aren't at the top already to avoid annoying jumps
            if (listState.firstVisibleItemIndex > 0) {
                listState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        topBar = { YTTopAppBar(onNavigateToSearch = onNavigateToSearch) }
    ) { innerPadding ->

        // 3. Ensure the PullToRefresh container wraps the scrollable content
        YTPullToRefreshBox (
            isRefreshing = isRefreshing,
            onRefresh = { videos.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                if (videos.loadState.refresh is LoadState.Loading && videos.itemCount == 0) {
                    items(5) {
                        VideoItemSkeleton()
                    }
                }

                items(
                    count = videos.itemCount,
                    key = videos.itemKey { it.id },
                    contentType = { "video_item" }
                ) { index ->
                    videos[index]?.let { video ->
                        VideoItem(
                            video = video,
                            onClick = { onPlayVideo(video.videoFile, video.id) },
                            onNavigateToChannel = { onNavigateToChannel(video.username, video.ownerId) }
                        )
                    }
                }

                if (videos.itemCount > 0) {
                    item(key = "footer") {
                        when (videos.loadState.append) {
                            is LoadState.Loading -> BottomLoader()
                            is LoadState.Error -> RetryFooter(onRetry = { videos.retry() })
                            else -> Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            // Initial Load / Empty State Error handling
            if (videos.loadState.refresh is LoadState.Error && videos.itemCount == 0) {
                ErrorScreen(
                    message = "Failed to load videos",
                    onRetry = { videos.retry() }
                )
            }

            else if (videos.loadState.refresh is LoadState.NotLoading && videos.itemCount == 0) {
                EmptyStateScreen(
                    message = "No videos found.",
                    onRetry = { videos.refresh() } // Let them try again!
                )
            }
        }
    }
}

@Composable
fun EmptyStateScreen(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: (() -> Unit)? = null, // Optional retry button
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // You can replace this with a relevant Painter/Icon
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try searching for something else or check back later.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(text = "Refresh")
            }
        }
    }
}

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 8f),
        MaterialTheme.colorScheme.surfaceVariant,
    )

    return this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnim.value, y = translateAnim.value)
        )
    )
}

@Composable
fun VideoItemSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // Thumbnail "Ghost" (16:9 Aspect Ratio)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .shimmerEffect()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar "Ghost"
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title Line 1
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle Line (Channel name & views)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}
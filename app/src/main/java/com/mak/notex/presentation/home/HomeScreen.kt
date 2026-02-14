package com.mak.notex.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.mak.notex.presentation.common.BottomLoader
import com.mak.notex.presentation.common.ErrorScreen
import com.mak.notex.presentation.common.FullScreenLoader
import com.mak.notex.presentation.common.RetryFooter
import com.mak.notex.presentation.common.VideoItem
import com.mak.notex.presentation.common.YTPullToRefreshIndicator
import com.mak.notex.presentation.navigation.LocalSnackbarHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPlayVideo: (String, String) -> Unit,
    onNavigateToChannel: (String, String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val videos = viewModel.videos.collectAsLazyPagingItems()
    val snackbarHostState = LocalSnackbarHostState.current
    val listState = rememberLazyListState()


    LaunchedEffect(key1 = videos.loadState) {
        if (videos.loadState.refresh is LoadState.Error) {
            val result = snackbarHostState.showSnackbar(
                message = "Error: " + (videos.loadState.refresh as LoadState.Error).error.message,
                actionLabel = "Retry"
            )
            if (result == SnackbarResult.ActionPerformed) {
                videos.retry()
            }
        }
    }

    val isRefreshing = videos.loadState.refresh is LoadState.Loading && videos.itemCount > 0

//    LaunchedEffect(videos.loadState.refresh) {
//        if (videos.loadState.refresh is LoadState.NotLoading && videos.itemCount > 0 ) {
//            listState.animateScrollToItem(0)
//        }
//    }


    YTPullToRefreshIndicator(
        isRefreshing = isRefreshing,
        onRefresh = { videos.refresh() },
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {

            items(
                count = videos.itemCount,
                key = videos.itemKey { it.id }
            ) { index ->
                videos[index]?.let { video ->
                    VideoItem(
                        video = video,
                        onClick = { onPlayVideo(video.videoFile, video.id) },
                        onNavigateToChannel = {
                            onNavigateToChannel(
                                video.username,
                                video.ownerId
                            )
                        }
                    )
                }
            }

            // Append loading state (at the end of the list)
            when (videos.loadState.append) {
                is LoadState.Loading -> {
                    item { BottomLoader() }
                }

                is LoadState.Error -> {
                    item {
                        RetryFooter(
                            onRetry = { videos.retry() }
                        )
                    }
                }

                else -> {}
            }
        }

        // Refresh state (initial load or manual refresh)
        when (videos.loadState.refresh) {
            is LoadState.Loading -> {
                if (videos.itemCount == 0) {
                    FullScreenLoader()
                }
            }

            is LoadState.Error -> {
                if (videos.itemCount == 0) {
                    ErrorScreen(
                        message = "Failed to load videos",
                        onRetry = { videos.retry() }
                    )
                }
            }

            else -> {}
        }
    }
}


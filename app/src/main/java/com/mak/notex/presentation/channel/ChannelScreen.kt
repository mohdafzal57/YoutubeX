package com.mak.notex.presentation.channel

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.mak.notex.domain.model.UserChannel
import com.mak.notex.domain.model.UserVideo
import com.mak.notex.presentation.common.AppScaffold
import com.mak.notex.presentation.common.BottomLoader
import com.mak.notex.presentation.common.FullScreenLoader
import com.mak.notex.presentation.common.RetryFooter
import com.mak.notex.presentation.common.ShareVideoButton
import com.mak.notex.utils.formatDuration
import com.mak.notex.presentation.navigation.LocalSnackbarHostState
import com.mak.notex.presentation.subscription.NotificationSettingsSheet

@Composable
fun ChannelScreen(
    onNavigateBack: () -> Unit,
    onPlayVideo: (String, String) -> Unit,
    viewModel: ChannelViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    var showBottomSheet by remember { mutableStateOf(false) }
    val videos = viewModel.videos.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is ChannelEvent.ShowError -> {
                    snackbarHostState.showSnackbar(it.message)
                }

                ChannelEvent.SubscriptionUpdated -> {
                    snackbarHostState.showSnackbar("Subscription updated")
                }
            }
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AppScaffold(
            title = uiState.profile?.username ?: "",
            showBackButton = true,
            onBackClick = onNavigateBack,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) { paddingValues ->

            if (uiState.isLoading) {
                FullScreenLoader()
                return@AppScaffold

            } else if (uiState.profile != null) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        // --- 1. Banner with Rounded Corners ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp) // Creates the "floating" banner look
                                .aspectRatio(3.5f) // Matches the slim profile of YouTube banners
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = uiState.profile?.coverImage ?: "",
                                contentDescription = "Channel Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    item {
                        ChannelHeaderSection(uiState.profile!!)
                    }


                    item {
                        YouTubeSubscribeButton(
                            isSubscribed = uiState.isSubscribed,
                            onClick = {
                                if (uiState.isSubscribed)
                                    showBottomSheet = true
                                else
                                    viewModel.onIntent(ChannelIntent.ToggleSubscription)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.padding(bottom = 16.dp))
                    }

                    item {
                        FilterChipsRow(
                            selectedSort = uiState.sortType,
                            onAction = viewModel::onIntent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(
                        count = videos.itemCount,
                        key = videos.itemKey { it.id }
                    ) { index ->
                        videos[index]?.let { video ->
                            VideoCard(
                                video = video,
                                onClick = { onPlayVideo(video.videoFile, video.id) },
                            )
                        }
                    }
                    when (videos.loadState.append) {
                        is LoadState.Loading -> {
                            item { BottomLoader() }
                        }

                        is LoadState.Error -> {
                            item {
                                RetryFooter(
                                    onRetry = {videos.retry()}
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }

        }
    }
    if (showBottomSheet) {
        NotificationSettingsSheet(
            onDismiss = { showBottomSheet = false },
            onUnsubscribe = {
                viewModel.onIntent(ChannelIntent.ToggleSubscription)
                showBottomSheet = false
            }
        )
    }
}
@Composable
fun ChannelHeaderSection(
    profile: UserChannel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with a specific background color for that "circular frame" look
        AsyncImage(
            model = profile.avatar,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F1B1B)), // Darker tint often seen behind logos
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Channel Name (Bold & Large)
            Text(
                text = profile.username,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Handle / Username
            Text(
                text = "@${profile.fullName.lowercase().replace(" ", "")}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Subscriber and Video count metadata
            Text(
                text = "${profile.subscribersCount} subscribers • ${profile.videosCount} videos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun FilterChipsRow(
    selectedSort: SortType,
    onAction: (ChannelIntent) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChipItem(
                text = "Latest",
                isSelected = selectedSort == SortType.LATEST,
                onClick = {
                    onAction(
                        ChannelIntent.OrderType(SortType.LATEST)
                    )
                }
            )
        }

        item {
            FilterChipItem(
                text = "Oldest",
                isSelected = selectedSort == SortType.OLDEST,
                onClick = {
                    onAction(
                        ChannelIntent.OrderType(SortType.OLDEST)
                    )
                }
            )
        }
    }
}
@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.onSurface // Black in light mode, White in dark
    } else {
        MaterialTheme.colorScheme.surfaceVariant // Soft gray
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.surface // Text color flips for contrast
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = onClick,
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(8.dp), // M3 uses 8.dp for small components
        modifier = Modifier.height(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
@Composable
fun VideoCard(video: UserVideo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.Top
    ) {
        // --- 1. Thumbnail with Duration Overlay ---
        Box(
            modifier = Modifier
                .width(160.dp) // Adjusted to match the aspect ratio in your image
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Duration Badge (Bottom Right)
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
            ) {
                Text(
                    text = video.duration,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // --- 2. Video Details ---
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Channel Name
            Text(
                text = video.username,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Views and Time
            Text(
                text = "${video.views} • ${video.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // --- 3. More Options Menu ---
        ShareVideoButton(video.videoFile)
    }
}
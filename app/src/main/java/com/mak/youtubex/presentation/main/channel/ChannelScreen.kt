package com.mak.youtubex.presentation.main.channel

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.mak.youtubex.domain.model.Post
import com.mak.youtubex.domain.model.UserChannel
import com.mak.youtubex.domain.model.UserVideo
import com.mak.youtubex.presentation.main.common.AppScaffold
import com.mak.youtubex.presentation.main.common.BottomLoader
import com.mak.youtubex.presentation.main.common.FullScreenLoader
import com.mak.youtubex.presentation.main.common.RetryFooter
import com.mak.youtubex.presentation.main.common.ShareVideoButton
import com.mak.youtubex.presentation.main.social_feed.CommunityPostCard
import com.mak.youtubex.presentation.main.subscription.NotificationSettingsSheet
import com.mak.youtubex.presentation.navigation.LocalSnackbarHostState

@Composable
fun ChannelScreen(
    onNavigateBack: () -> Unit,
    onPlayVideo: (String, String) -> Unit,
    onNavigateToPost: (String) -> Unit = {}, // Added for potential navigation from posts
    viewModel: ChannelViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val videos = viewModel.videos.collectAsLazyPagingItems()
    val posts = viewModel.userPosts.collectAsLazyPagingItems()
    val snackbarHostState = LocalSnackbarHostState.current

    var showBottomSheet by remember { mutableStateOf(false) }

    // Side Effects
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is ChannelEvent.ShowError) {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AppScaffold(
            title = uiState.profile?.username.orEmpty(),
            showBackButton = true,
            onBackClick = onNavigateBack
        ) { padding ->

            ChannelContent(
                uiState = uiState,
                videos = videos,
                posts = posts,
                padding = padding,
                onIntent = viewModel::onIntent,
                onPlayVideo = onPlayVideo,
                onSubscribeClick = {
                    if (uiState.isSubscribed) {
                        showBottomSheet = true
                    } else {
                        viewModel.onIntent(ChannelIntent.ToggleSubscription)
                    }
                }
            )
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
private fun ChannelContent(
    uiState: ChannelProfileState,
    videos: LazyPagingItems<UserVideo>,
    posts: LazyPagingItems<Post>,
    padding: PaddingValues,
    onIntent: (ChannelIntent) -> Unit,
    onPlayVideo: (String, String) -> Unit,
    onSubscribeClick: () -> Unit
) {
    when {
        uiState.isLoading -> {
            FullScreenLoader()
        }

        uiState.profile != null -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {

                // 🔹 Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .aspectRatio(3.5f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = uiState.profile.coverImage ?: "",
                            contentDescription = "Channel Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // 🔹 Header
                item {
                    ChannelHeaderSection(uiState.profile)
                }

                // 🔹 Subscribe Button
                item {
                    Column {
                        YouTubeSubscribeButton(
                            isSubscribed = uiState.isSubscribed,
                            onClick = onSubscribeClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // 🔹 Tabs
                item {
                    ChannelTabs(
                        selectedTab = uiState.contentType,
                        onTabSelected = { onIntent(ChannelIntent.Content(it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (uiState.contentType == ContentType.VIDEOS) {
                    // 🔹 Video Filters
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        FilterChipsRow(
                            selectedSort = uiState.sortType,
                            onAction = onIntent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 🔹 Video List
                    items(
                        count = videos.itemCount,
                        key = videos.itemKey { it.id }
                    ) { index ->
                        videos[index]?.let { video ->
                            VideoCard(
                                video = video,
                                onClick = {
                                    onPlayVideo(video.videoFile, video.id)
                                }
                            )
                        }
                    }

                    // 🔹 Video Paging Footer
                    when (videos.loadState.append) {
                        is LoadState.Loading -> {
                            item { BottomLoader() }
                        }

                        is LoadState.Error -> {
                            item { RetryFooter(onRetry = { videos.retry() }) }
                        }

                        else -> Unit
                    }
                } else {
                    // 🔹 Posts List
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    items(
                        count = posts.itemCount,
                        key = posts.itemKey { it.id }
                    ) { index ->
                        posts[index]?.let { post ->
                            CommunityPostCard(
                                post = post,
                                onAction = { /* Handle post actions if needed */ },
                                onNavigateToChannel = { /* Already on channel */ },
                                onCommentClick = { /* Handle comments if needed */ }
                            )
                        }
                    }

                    // 🔹 Posts Paging Footer
                    when (posts.loadState.append) {
                        is LoadState.Loading -> {
                            item { BottomLoader() }
                        }

                        is LoadState.Error -> {
                            item { RetryFooter(onRetry = { posts.retry() }) }
                        }

                        else -> Unit
                    }
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Something went wrong",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun ChannelTabs(
    selectedTab: ContentType,
    onTabSelected: (ContentType) -> Unit
) {
    val tabs = ContentType.entries.toTypedArray()

    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        divider = {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        },
    ) {
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text(
                    text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
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
        AsyncImage(
            model = profile.avatar,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F1B1B)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
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

            Text(
                text = "@${profile.fullName.lowercase().replace(" ", "")}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )

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
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = onClick,
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(8.dp),
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
        Box(
            modifier = Modifier
                .width(160.dp)
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

            Text(
                text = "${video.views} views • ${video.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ShareVideoButton(video.videoFile)
    }
}
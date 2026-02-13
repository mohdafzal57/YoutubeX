package com.mak.notex.presentation.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import com.mak.notex.presentation.common.LoadingScreen
import com.mak.notex.presentation.home.BottomLoader
import com.mak.notex.presentation.home.RetryFooter
import com.mak.notex.presentation.navigation.LocalSnackbarHostState
import com.mak.notex.presentation.subscription.NotificationSettingsSheet

@Composable
fun ChannelDetailsScreen(
    viewModel: ChannelDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onPlayVideo: (String, String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    var showBottomSheet by remember { mutableStateOf(false) }
    val videos = viewModel.videos.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                is ChannelEffect.ShowError -> {
                    snackbarHostState.showSnackbar(it.message)
                }

                ChannelEffect.SubscriptionUpdated -> {
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
        ) { paddingValues ->

            if (uiState.isLoading) {
                LoadingScreen()
                return@AppScaffold

            } else if (uiState.profile != null) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // 1. Channel Banner
                    item {
                        AsyncImage(
                            model = uiState.profile?.coverImage
                                ?: "https://via.placeholder.com/800x200/black/red?text=Every+Soul+Will+Taste+Death",
                            contentDescription = "Channel Banner",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp), // Adjust height as per screenshot
                            contentScale = ContentScale.Crop
                        )
                    }

                    // 2. Channel Info Header
                    item {
                        ChannelHeaderSection(uiState.profile!!)
                    }

                    // 3. Subscribe Button
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
                        Spacer(modifier = Modifier.padding(bottom = 8.dp))
                    }

                    // 4. Filter Chips (Latest, Popular, Oldest)
                    item {
                        FilterChipsRow(
                            selectedSort = uiState.sortType,
                            onAction = viewModel::onIntent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 5. Video List
                    items(
                        count = videos.itemCount,
                        key = videos.itemKey { it.id }
                    ) { index ->
                        videos[index]?.let { video ->
                            VideoListItem(
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
                                RetryFooter { videos.retry() }
                            }
                        }

                        else -> {}
                    }
                }
            }

        }
        // We use a single LazyColumn for the entire page so the header scrolls with the content
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
fun ChannelHeaderSection(profile: UserChannel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar
            AsyncImage(
                model = profile.avatar,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text Info
            Column {
                Text(
                    text = profile.username,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )

                // Using AnnotatedString to style the bullet point if needed, or simple concatenation
                Text(
                    text = ("@" + profile.username),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                )
                Text(
                    text = "${profile.subscribersCount} subscribers • 1.3K videos",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description Snippet
        Text(
            text = buildAnnotatedString {
                append("Welcome to your official YouTube channel, ${profile.username} . ...")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("more")
                }
            },
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray, fontSize = 13.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Link
//        Text(
//            text = "wa.me/ 97333456770",
//            style = MaterialTheme.typography.bodyMedium.copy(
//                fontWeight = FontWeight.Bold,
//                fontSize = 13.sp
//            )
//        )
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
    Surface(
        onClick = onClick, // ✅ clickable
        color = if (isSelected) Color.Black else Color(0xFFF2F2F2),
        contentColor = if (isSelected) Color.White else Color.Black,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun VideoListItem(
    video: UserVideo,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Gap between videos
            .clickable { onClick() }
    ) {
        // 1. Thumbnail Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            )
            // Duration Badge
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Text(
                    text = video.duration.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        // 2. Video Details Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 12.dp, end = 12.dp),
        ) {
            // Title and Metadata
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                )
            }

            // Three dots icon
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = Color.Black
                )
            }
        }
    }
}

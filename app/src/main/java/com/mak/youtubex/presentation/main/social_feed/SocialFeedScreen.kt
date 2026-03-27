package com.mak.youtubex.presentation.main.social_feed

import android.R.attr.onClick
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.mak.youtubex.presentation.main.common.YTTopAppBar
import com.mak.youtubex.presentation.main.home.shimmerEffect
import com.mak.youtubex.presentation.ui.theme.ColorLike
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    viewModel: SocialFeedViewModel = hiltViewModel()
) {
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SocialFeedEvent.ShowError -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            YTTopAppBar(
                scrollBehavior = scrollBehavior,
                onNavigateToSearch = { }
            )
        },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Always show the list if we have items
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = posts.itemCount,
                    key = posts.itemKey { it.id },
                    contentType = posts.itemContentType { "post" }
                ) { index ->
                    val post = posts[index]
                    if (post != null) {
                        PostItem(
                            post = post,
                            onAction = viewModel::onAction
                        )
                    }
                }

                // 2. Show loading spinner at the bottom when fetching more (APPEND)
                if (posts.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // 3. Overlay the Shimmer ONLY on the very first load (no data yet)
            if (posts.loadState.refresh is LoadState.Loading && posts.itemCount == 0) {
                ShimmerList()
            }

            // 4. Show an error UI ONLY if the list is empty and we hit an error
            if (posts.loadState.refresh is LoadState.Error && posts.itemCount == 0) {
                val error = (posts.loadState.refresh as LoadState.Error).error
                Box(modifier = Modifier.align(Alignment.Center)) {
                    TextButton(onClick = { posts.retry() }) {
                        Text(text = "Offline: ${error.localizedMessage}\nTap to Retry")
                    }
                }
            }
        }
    }
}


@Composable
private fun PostItem(
    post: Post,
    onAction: (SocialFeedAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Top section (avatar + text)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PostAvatar(post.avatarUrl)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                PostHeader(post.username, post.timestamp)

                Text(
                    text = post.body,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // 🔥 FULL WIDTH IMAGE (no avatar constraint)
        if (post.imageUrls.isNotEmpty()) {
            ImagePager(
                images = post.imageUrls,
                onDoubleTap = {
                    onAction(SocialFeedAction.ToggleLike(post.id))
                }
            )
        }

        // Actions (aligned with text, not full bleed)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 56.dp, end = 8.dp, top = 8.dp), // align with text start
        ) {
            PostActions(
                isLiked = post.isLiked,
                likeCount = post.likeCount,
                commentCount = post.commentCount,
                onLikeToggle = {
                    onAction(SocialFeedAction.ToggleLike(post.id))
                }
            )
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
fun ImagePager(
    images: List<String>,
    onDoubleTap: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(start = 52.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = images,
            key = { it },
            contentType = { "image" }
        ) {
            AsyncImage(
                model = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillParentMaxWidth(0.85f)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = { onDoubleTap() })
                    }
            )
        }
    }
}

@Composable
fun ShimmerList() {
    LazyColumn {
        items(5) {
            ThreadShimmerItem()

            // Optional: Add a subtle divider between the fake posts just like the real app
            HorizontalDivider(
                Modifier,
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun ThreadShimmerItem() {
    // IntrinsicSize.Min is the secret to making the vertical line match the content height
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(16.dp)
    ) {
        // Left Column: Avatar and the vertical connecting line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // The vertical thread line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(1.dp))
                    .shimmerEffect()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Column: All the text and buttons
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header (Name and Time)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.width(140.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Box(modifier = Modifier.width(40.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Text lines
            Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(0.85f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                repeat(4) {
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).shimmerEffect())
                }
            }

            // Extra padding at the bottom so the line extends past the buttons
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
private fun PostAvatar(url: String) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
    )
}
@Composable
private fun PostHeader(username: String, timestamp: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = username,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f, fill = false)
        )

        Text("·", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Text(
            text = timestamp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PostActions(
    isLiked: Boolean,
    likeCount: Int,
    commentCount: Int,
    onLikeToggle: () -> Unit,
) {
    val tint = if (isLiked) ColorLike else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        ActionButton(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            count = likeCount,
            tint = tint,
            onClick = onLikeToggle
        )

        ActionButton(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            count = commentCount,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = { /* TODO */ }
        )

        IconButton(
            onClick = {},
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ActionButton(
    imageVector: ImageVector,
    count: Int,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }

        if (count > 0) {
            Text(
                text = formatCount(count),
                style = MaterialTheme.typography.bodySmall,
                color = tint
            )
        }
    }
}

private fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> "%.1fM".format(count / 1_000_000.0).trimEnd('0').trimEnd('.')
    count >= 1_000 -> "%.1fk".format(count / 1_000.0).trimEnd('0').trimEnd('.')
    else -> count.toString()
}

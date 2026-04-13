package com.mak.youtubex.presentation.main.social_feed

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.mak.youtubex.R
import com.mak.youtubex.domain.model.Post
import com.mak.youtubex.presentation.main.common.EmptyState
import com.mak.youtubex.presentation.main.common.VideoSearchIcon
import com.mak.youtubex.presentation.main.common.YTLogo
import com.mak.youtubex.presentation.main.common.YTPullToRefreshBox
import com.mak.youtubex.presentation.main.common.YTTopAppBar
import com.mak.youtubex.presentation.main.common.shimmerEffect
import com.mak.youtubex.presentation.ui.theme.ColorLike
import com.mak.youtubex.presentation.ui.theme.YTTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToChannel: (username: String) -> Unit,
    viewModel: SocialFeedViewModel = hiltViewModel()
) {
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCommentsForPost by remember { mutableStateOf<Post?>(null) }

    val isUserRefreshing = posts.loadState.refresh is LoadState.Loading && posts.itemCount > 0

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            YTTopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = { YTLogo() },
                actions = {
                    VideoSearchIcon(onClick = onNavigateToSearch)
                }
            )
        },
    ) { innerPadding ->
        if (showCommentsForPost != null) {
            ModalBottomSheet(
                onDismissRequest = { showCommentsForPost = null },
                sheetState = sheetState,
                dragHandle = null,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                CommentsSheetContent(
                    postId = showCommentsForPost!!.id,
                    commentsCount = showCommentsForPost!!.commentCount,
                    viewModel = viewModel,
                    onClose = { showCommentsForPost = null }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            YTPullToRefreshBox(
                isRefreshing = isUserRefreshing,
                onRefresh = { posts.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        count = posts.itemCount,
                        key = posts.itemKey { it.id },
                        contentType = posts.itemContentType { "post" }
                    ) { index ->
                        posts[index]?.let { post ->
                            CommunityPostCard(
                                post = post,
                                onAction = viewModel::onAction,
                                onCommentClick = { showCommentsForPost = post },
                                onNavigateToChannel = { onNavigateToChannel(post.username) }
                            )
                        }
                    }

                    if (posts.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }

            if (posts.loadState.refresh is LoadState.Loading && posts.itemCount == 0) {
                PostSkeleton()
            }

            if (posts.loadState.refresh is LoadState.Error && posts.itemCount == 0) {
                EmptyState(
                    title = stringResource(R.string.empty_posts_title),
                    message = stringResource(R.string.no_posts_found),
                    icon = Icons.Default.HideImage,
                    actionText = stringResource(R.string.refresh),
                    onAction = { posts.refresh() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun CommunityPostCard(
    post: Post,
    onAction: (SocialFeedAction) -> Unit,
    onNavigateToChannel: () -> Unit,
    onCommentClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        CommunityPostHeader(
            avatarUrl = post.avatarUrl,
            username = post.username,
            timestamp = post.timestamp,
            onAvatarClick = onNavigateToChannel,
            onMoreClick = { }
        )

        if (post.body.isNotBlank()) {
            CommunityPostText(
                text = post.body,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            )
        }

        if (post.imageUrls.isNotEmpty()) {
            CommunityPostMedia(
                images = post.imageUrls,
                onDoubleTap = { onAction(SocialFeedAction.ToggleLike(post.id)) }
            )
        }

        CommunityPostInteraction(
            isLiked = post.isLiked,
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            onLikeToggle = { onAction(SocialFeedAction.ToggleLike(post.id)) },
            onDislike = { },
            onShare = { sharePost(context, post.id, post.username, post.body) },
            onCommentClick = onCommentClick
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        )
    }
}

@Composable
private fun CommunityPostHeader(
    avatarUrl: String,
    username: String,
    timestamp: String,
    onAvatarClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onAvatarClick)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = username.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = onMoreClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun CommunityPostText(
    text: String,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 5
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp
            ),
            maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { if (!expanded) isOverflowing = it.hasVisualOverflow }
        )

        if (isOverflowing || expanded) {
            Text(
                text = if (expanded) "Show less" else "Read more",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { expanded = !expanded }
            )
        }
    }
}

@Composable
private fun CommunityPostMedia(
    images: List<String>,
    modifier: Modifier = Modifier,
    onDoubleTap: (index: Int) -> Unit = {}
) {
    if (images.isEmpty()) return

    if (images.size == 1) {
        CommunityPostSingleImage(
            url = images[0],
            modifier = modifier,
            onDoubleTap = { onDoubleTap(0) }
        )
    } else {
        CommunityPostCarousel(
            images = images,
            modifier = modifier,
            onDoubleTap = onDoubleTap
        )
    }
}

@Composable
private fun CommunityPostSingleImage(
    url: String,
    modifier: Modifier = Modifier,
    onDoubleTap: () -> Unit
) {
    var isError by remember { mutableStateOf(false) }

    if (isError) {
        CommunityImageError(
            modifier = modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        return
    }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = null,
        placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
        onError = { isError = true },
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, 350.dp)
            .pointerInput(url) {
                detectTapGestures(onDoubleTap = { onDoubleTap() })
            }
    )
}

@Composable
private fun CommunityPostCarousel(
    images: List<String>,
    modifier: Modifier = Modifier,
    onDoubleTap: (index: Int) -> Unit
) {
    val listState = rememberLazyListState()
    val snapFling = rememberSnapFlingBehavior(lazyListState = listState)
    val total = images.size

    LazyRow(
        state = listState,
        flingBehavior = snapFling,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            items = images,
            key = { _, url -> url }
        ) { index, imageUrl ->
            CommunityCarouselItem(
                url = imageUrl,
                badgeText = "${index + 1}/$total",
                onDoubleTap = { onDoubleTap(index) }
            )
        }
    }
}

@Composable
private fun CommunityCarouselItem(
    url: String,
    badgeText: String,
    onDoubleTap: () -> Unit
) {
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(310.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(url) {
                detectTapGestures(onDoubleTap = { onDoubleTap() })
            }
    ) {
        if (isError) {
            CommunityImageError(modifier = Modifier.fillMaxSize())
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                onError = { isError = true },
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            shape = RoundedCornerShape(4.dp),
            color = Color.Black.copy(alpha = 0.6f)
        ) {
            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun CommunityImageError(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.BrokenImage,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun CommunityPostInteraction(
    isLiked: Boolean,
    likeCount: String,
    commentCount: String,
    onLikeToggle: () -> Unit,
    onDislike: () -> Unit,
    onShare: () -> Unit,
    onCommentClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LikeDislikeCombinedPill(
            isLiked = isLiked,
            likeCount = likeCount,
            onLikeToggle = onLikeToggle,
            onDislike = onDislike
        )

        CommunityInteractionChip(
            icon = Icons.Outlined.ChatBubbleOutline,
            label = if (commentCount == "0") null else commentCount,
            onClick = onCommentClick
        )

        Spacer(modifier = Modifier.weight(1f))

        CommunityInteractionChip(
            icon = Icons.AutoMirrored.Outlined.Send,
            label = "Share",
            onClick = onShare
        )
    }
}

@Composable
private fun LikeDislikeCombinedPill(
    isLiked: Boolean,
    likeCount: String,
    onLikeToggle: () -> Unit,
    onDislike: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onLikeToggle
                    )
                    .padding(start = 12.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = null,
                    tint = if (isLiked) ColorLike else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
                if (likeCount != "0") {
                    Text(
                        text = likeCount,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            )

            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDislike
                    )
                    .padding(start = 10.dp, end = 12.dp, top = 6.dp, bottom = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ThumbDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CommunityInteractionChip(
    icon: ImageVector,
    label: String? = null,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
            if (!label.isNullOrEmpty()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun sharePost(
    context: Context,
    postId: String,
    username: String,
    body: String
) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        val deepLink = "https://youtubex.com/post/$postId"
        val shareText = "Check out this post from $username on YoutubeX:\n\n$body\n\n$deepLink"
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share post via"))
}

@Preview
@Composable
fun PostSkeleton() {
    LazyColumn(userScrollEnabled = false) {
        items(4) {
            CommunityPostShimmer()
        }
    }
}

@Composable
fun CommunityPostShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityPostCardPreview() {
    YTTheme(darkTheme = true) {
        CommunityPostCard(
            post = Post(
                id = "",
                avatarUrl = "",
                username = "Mohammad Faisal",
                timestamp = "1h ago",
                body = "Hey Developers, welcome to my YoutubeX App.",
                imageUrls = listOf("", ""),
                likeCount = "899",
                commentCount = "200",
                isLiked = true
            ),
            onAction = {},
            onNavigateToChannel = {},
            onCommentClick = {}
        )
    }
}

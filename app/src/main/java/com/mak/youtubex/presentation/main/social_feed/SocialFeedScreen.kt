package com.mak.youtubex.presentation.main.social_feed

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.mak.youtubex.domain.model.Post
import com.mak.youtubex.presentation.main.common.YTPullToRefreshBox
import com.mak.youtubex.presentation.main.common.YTTopAppBar
import com.mak.youtubex.presentation.main.home.EmptyStateScreen
import com.mak.youtubex.presentation.main.home.shimmerEffect
import com.mak.youtubex.presentation.navigation.LocalSnackbarHostState
import com.mak.youtubex.presentation.ui.theme.ColorLike
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    viewModel: SocialFeedViewModel = hiltViewModel()
) {
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val snackbarHostState = LocalSnackbarHostState.current

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCommentsForPost by remember { mutableStateOf<Post?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SocialFeedEvent.ShowError -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }

                is SocialFeedEvent.CommentAdded -> {

                }
            }
        }
    }

    // True ONLY when user triggered a pull-to-refresh on an already-populated list.
    val isUserRefreshing = posts.loadState.refresh is LoadState.Loading
            && posts.itemCount > 0

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
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        count = posts.itemCount,
                        key = posts.itemKey { it.id },
                        contentType = posts.itemContentType { "post" }
                    ) { index ->
                        posts[index]?.let { post ->
                            PostItem(
                                post = post,
                                onAction = viewModel::onAction,
                                onCommentClick = {
                                    showCommentsForPost = post
                                }
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
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            if (posts.loadState.refresh is LoadState.Loading && posts.itemCount == 0) {
                PostSkeleton()
            }

            if (posts.loadState.refresh is LoadState.Error && posts.itemCount == 0) {
                EmptyStateScreen(
                    message = "Failed to load posts",
                    onRetry = { posts.refresh() }
                )
            }
        }
    }
}

@Composable
private fun PostItem(
    post: Post,
    onAction: (SocialFeedAction) -> Unit,
    onCommentClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            AsyncImage(
                model = post.avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = post.username,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = post.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (post.body.isNotBlank()) {
                    Text(
                        text = post.body,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 20.sp,
                            letterSpacing = 0.1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (post.imageUrls.isNotEmpty()) {
            ImagePager(
                images = post.imageUrls,
                onDoubleTap = {
                    onAction(SocialFeedAction.ToggleLike(post.id))
                }
            )
        }

        PostActions(
            isLiked = post.isLiked,
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            onLikeToggle = {
                onAction(SocialFeedAction.ToggleLike(post.id))
            },
            onSharePost = {
                sharePost(
                    context,
                    post.username,
                    post.body,
                    post.imageUrls.firstOrNull()
                )
            },
            onCommentClick = { onCommentClick() }
        )

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}


@Composable
fun ImagePager(
    images: List<String>,
    onDoubleTap: () -> Unit
) {
    val firstImageRatio = remember { mutableStateOf(1f) }
    LazyRow(
        contentPadding = PaddingValues(start = 60.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = images,
            key = { it },
            contentType = { "image" }
        ) { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .heightIn(max = 288.dp)
                    .aspectRatio(firstImageRatio.value)
                    .clip(RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = { onDoubleTap() })
                    },
                onSuccess = { state ->
                    if (firstImageRatio.value == 1f) {
                        val d = state.result.drawable
                        val w = d.intrinsicWidth
                        val h = d.intrinsicHeight
                        if (w > 0 && h > 0) {
                            firstImageRatio.value = w.toFloat() / h
                        }
                    }
                }
            )
        }
    }
}


@Composable
private fun PostActions(
    isLiked: Boolean,
    likeCount: String,
    commentCount: String,
    onLikeToggle: () -> Unit,
    onCommentClick: () -> Unit,
    onSharePost: () -> Unit
) {
    val tint = if (isLiked) ColorLike else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 56.dp)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
            onClick = onCommentClick
        )

        IconButton(
            onClick = onSharePost,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share post",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun sharePost(
    context: android.content.Context,
    username: String,
    body: String,
    imageUrl: String?
) {
    val sendIntent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        val shareText = "Check out this post from $username on YoutubeX:\n\n$body" +
                (if (imageUrl != null) "\n\n$imageUrl" else "")

        putExtra(android.content.Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = android.content.Intent.createChooser(sendIntent, "Share post via")
    context.startActivity(shareIntent)
}


@Composable
private fun ActionButton(
    imageVector: ImageVector,
    count: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }

        if (count != "0") {
            Text(
                text = count,
                style = MaterialTheme.typography.labelSmall,
                color = tint.copy(alpha = 0.9f)
            )
        }
    }
}

@Preview
@Composable
fun PostSkeleton() {
    LazyColumn(
        userScrollEnabled = false
    ) {
        items(4) {
            PostsShimmer()
        }
    }
}

@Composable
fun PostsShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )
        }
    }
}
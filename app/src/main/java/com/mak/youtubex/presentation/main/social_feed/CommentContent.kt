package com.mak.youtubex.presentation.main.social_feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.mak.youtubex.domain.model.Comment
import com.mak.youtubex.presentation.main.common.UserAvatarIcon
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CommentsSheetContent(
    postId: String,
    commentsCount: String,
    viewModel: SocialFeedViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val commentsFlow = remember(postId) { viewModel.getComments(postId) }
    val comments = commentsFlow.collectAsLazyPagingItems()
    val isSendingComment by viewModel.isSendingComment.collectAsStateWithLifecycle()
    var commentText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val avatar by viewModel.avatar.collectAsStateWithLifecycle()


    // Scroll to top after comment is successfully added
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (event is SocialFeedEvent.CommentAdded && event.postId == postId) {
                comments.refresh()
                listState.animateScrollToItem(0)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxHeight(0.75f)
            .navigationBarsPadding()
            .imePadding()
    ) {
        CommentsHeader(
            commentsCount = commentsCount,
            onClose = onClose
        )

        HorizontalDivider(thickness = 0.5.dp)

        CommentsLazyColumn(
            comments = comments,
            listState = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        HorizontalDivider(thickness = 0.5.dp)

        CommentInputRow(
            commentText = commentText,
            isSendingComment = isSendingComment,
            onTextChange = { commentText = it },
            onSend = {
                viewModel.onAction(SocialFeedAction.AddComment(postId, commentText))
                commentText = ""
            },
            avatar = avatar,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
private fun CommentsHeader(
    commentsCount: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Comments",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = commentsCount,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onClose) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
    }
}

@Composable
private fun CommentsLazyColumn(
    comments: LazyPagingItems<Comment>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when {
            comments.loadState.refresh is LoadState.Loading && comments.itemCount == 0 -> {
                item(key = "loading_full") {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            comments.loadState.refresh is LoadState.Error && comments.itemCount == 0 -> {
                item(key = "error_full") {
                    val error = (comments.loadState.refresh as LoadState.Error).error
                    CommentsErrorState(
                        message = error.localizedMessage ?: "Failed to load comments",
                        onRetry = { comments.retry() }
                    )
                }
            }

            else -> {
                items(
                    count = comments.itemCount,
                    // itemKey extension handles null gracefully
                    key = comments.itemKey { it.id },
                    contentType = comments.itemContentType { "comment" }
                ) { index ->
                    comments[index]?.let { comment ->
                        CommentItem(comment = comment)
                    }
                }
            }
        }

        if (comments.loadState.append is LoadState.Loading) {
            item(key = "loading_append") {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }

        if (comments.loadState.append is LoadState.Error) {
            item(key = "error_append") {
                TextButton(
                    onClick = { comments.retry() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun CommentsErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        TextButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun CommentInputRow(
    avatar: String?,
    commentText: String,
    isSendingComment: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val canSend = commentText.isNotBlank() && !isSendingComment

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Current user avatar placeholder — replace with actual user avatar
        UserAvatarIcon(avatar, false)

        OutlinedTextField(
            value = commentText,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            enabled = !isSendingComment,
            placeholder = {
                Text(
                    text = "Add a comment...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = RoundedCornerShape(24.dp),
            trailingIcon = {
                when {
                    isSendingComment -> CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    canSend -> IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onSend()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send comment",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            maxLines = 4,
            singleLine = false
        )
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = comment.user.avatar,
            contentDescription = "${comment.user.username} avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@${comment.user.username}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = comment.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            CommentActions(
                likesCount = comment.likesCount,
                repliesCount = comment.repliesCount
            )
        }
    }
}

@Composable
private fun CommentActions(
    likesCount: Int,
    repliesCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "$likesCount likes",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (likesCount > 0) {
                Text(
                    text = likesCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (repliesCount > 0) {
            Text(
                text = "$repliesCount ${if (repliesCount == 1) "reply" else "replies"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
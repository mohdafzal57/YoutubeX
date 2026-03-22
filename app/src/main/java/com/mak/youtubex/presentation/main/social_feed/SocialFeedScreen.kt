package com.mak.youtubex.presentation.main.social_feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mak.youtubex.presentation.ui.theme.ColorLike

// ─────────────────────────────────────────────
// Design Tokens
// ─────────────────────────────────────────────

private val Spacing4 = 4.dp
private val Spacing8 = 8.dp
private val Spacing12 = 12.dp
private val Spacing16 = 16.dp

// ─────────────────────────────────────────────
// UI Model
// ─────────────────────────────────────────────



// ─────────────────────────────────────────────
// Sample Data
// ─────────────────────────────────────────────

val samplePosts = listOf(
    Post(
        id = "1",
        avatarUrl = "https://i.pravatar.cc/150?img=1",
        username = "Maya Chen",
        handle = "@mayachen",
        timestamp = "2h",
        body = "Just shipped our new design system after 6 months of iteration. The key insight? Constraints breed creativity. Fewer components, more expressiveness.",
        imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800",
        likeCount = 2400,
        commentCount = 184,
        shareCount = 841,
    ),
    Post(
        id = "2",
        avatarUrl = "https://i.pravatar.cc/150?img=11",
        username = "Alex Rivera",
        handle = "@alexrivera",
        timestamp = "4h",
        body = "Hot take: the best engineers I know spend 40% of their time reading code, not writing it. Understanding > output velocity every single time.\n\nThe fastest path to shipping is fully grasping what you're changing first.",
        likeCount = 5100,
        commentCount = 392,
        shareCount = 2200,
    ),
    Post(
        id = "3",
        avatarUrl = "https://i.pravatar.cc/150?img=32",
        username = "Sam Okafor",
        handle = "@samokafor",
        timestamp = "6h",
        body = "Golden hour never disappoints. Found this spot hiking the Sierra Nevada last weekend — completely off-trail, completely worth it. 🏔️",
        imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
        likeCount = 18200,
        commentCount = 763,
        shareCount = 4900,
    ),
    Post(
        id = "4",
        avatarUrl = "https://i.pravatar.cc/150?img=47",
        username = "Jordan Kim",
        handle = "@jordankim",
        timestamp = "9h",
        body = "Just learned that Jetpack Compose recomposition happens more often than most devs realize. Profiling your composables isn't optional if you care about 60fps. Here's what I found after a week of digging 👇",
        likeCount = 1700,
        commentCount = 218,
        shareCount = 509,
    ),
    Post(
        id = "5",
        avatarUrl = "https://i.pravatar.cc/150?img=23",
        username = "Priya Patel",
        handle = "@priyapatel",
        timestamp = "12h",
        body = "Visualizing attention heads in transformer models is genuinely beautiful. Each head learns to focus on completely different linguistic patterns. Language is geometry. 🤯",
        imageUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=800",
        likeCount = 9300,
        commentCount = 1100,
        shareCount = 3400,
    ),
    Post(
        id = "6",
        avatarUrl = "https://i.pravatar.cc/150?img=57",
        username = "Lena Müller",
        handle = "@lenamuller",
        timestamp = "1d",
        body = "Reminder that good API design is a form of empathy. Every method name, every error message, every default value is a tiny decision about who the developer using your library is and what they need.",
        likeCount = 3800,
        commentCount = 290,
        shareCount = 1600,
    ),
)


// ─────────────────────────────────────────────
// Root Screen
// ─────────────────────────────────────────────

@Composable
fun SocialFeedScreen(
    viewModel: SocialFeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = uiState.posts,
            key = { it.id },
            contentType = { "post" }
        ) { post ->

            val isLiked = post.id in uiState.likedIds

            PostItem(
                post = post,
                isLiked = isLiked,
                onLikeToggle = { viewModel.onLikeToggle(post.id) }
            )
        }
    }
}
// ─────────────────────────────────────────────
// Post Item
// ─────────────────────────────────────────────

@Composable
fun PostItem(
    post: Post,
    isLiked: Boolean,
    onLikeToggle: () -> Unit,
) {
    val likeCount = post.likeCount + if (isLiked) 1 else 0

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            PostAvatar(post.avatarUrl)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {

                PostHeader(post.username, post.timestamp)

                PostBody(post.body, post.imageUrl)

                PostActions(
                    isLiked = isLiked,
                    likeCount = likeCount,
                    commentCount = post.commentCount,
                    shareCount = post.shareCount,
                    onLikeToggle = onLikeToggle
                )
            }
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
fun PostAvatar(url: String) {
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
fun PostHeader(
    username: String,
    timestamp: String
) {
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
        Text(timestamp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


@Composable
fun PostBody(
    body: String,
    imageUrl: String?
) {
    Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge
    )

    if (imageUrl != null) {
        Spacer(Modifier.height(8.dp))

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}


@Composable
fun PostActions(
    isLiked: Boolean,
    likeCount: Int,
    commentCount: Int,
    shareCount: Int,
    onLikeToggle: () -> Unit,
) {
    val tint = if (isLiked) ColorLike
    else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        ActionButton(
            imageVector = if (isLiked)
                Icons.Filled.Favorite
            else
                Icons.Outlined.FavoriteBorder,
            count = likeCount,
            tint = tint,
            onClick = onLikeToggle
        )

        ActionButton(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            count = commentCount,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = {}
        )

        ActionButton(
            imageVector = Icons.Outlined.Share,
            count = shareCount,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = {}
        )
    }
}

@Composable
fun ActionButton(
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
// ─────────────────────────────────────────────
// Utilities
// ─────────────────────────────────────────────

private fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> "%.1fM".format(count / 1_000_000.0).trimEnd('0').trimEnd('.')
    count >= 1_000 -> "%.1fk".format(count / 1_000.0).trimEnd('0').trimEnd('.')
    else -> count.toString()
}
// ─────────────────────────────────────────────
// Top App Bar
// ─────────────────────────────────────────────

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FeedTopBar() {
        TopAppBar(
            title = {
                Text(
                    text = "Stream",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
            ),
            windowInsets = WindowInsets.statusBars,
        )
    }

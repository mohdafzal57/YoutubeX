package com.mak.notex.presentation.main.social_feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ─────────────────────────────────────────────
// Design Tokens
// ─────────────────────────────────────────────

private val ColorBackground    = Color(0xFF0F0F0F)
private val ColorSurface       = Color(0xFF121212)
private val ColorAccent        = Color(0xFF1D9BF0)
private val ColorSecondaryText = Color(0xFF71767B)
private val ColorDivider       = Color(0xFF1F1F1F)
private val ColorLike          = Color(0xFFF91880)

private val Spacing4  =  4.dp
private val Spacing8  =  8.dp
private val Spacing12 = 12.dp
private val Spacing16 = 16.dp

// ─────────────────────────────────────────────
// UI Model
// ─────────────────────────────────────────────

@Stable
data class Post(
    val id: String,
    val avatarUrl: String,
    val username: String,
    val handle: String,
    val timestamp: String,
    val body: String,
    val imageUrl: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
)

// ─────────────────────────────────────────────
// Sample Data
// ─────────────────────────────────────────────

private val samplePosts = listOf(
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
// Theme
// ─────────────────────────────────────────────

private val FeedColorScheme = darkColorScheme(
    background    = ColorBackground,
    surface       = ColorSurface,
    primary       = ColorAccent,
    onBackground  = Color(0xFFE7E9EA),
    onSurface     = Color(0xFFE7E9EA),
    surfaceVariant = ColorSurface,
)

@Composable
fun SocialFeedTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FeedColorScheme,
        content = content,
    )
}

// ─────────────────────────────────────────────
// Root Screen
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    posts: List<Post> = samplePosts,
) {
    SocialFeedTheme {
        val likedIds = remember { mutableStateMapOf<String, Boolean>() }
        val likeCounts = remember {
            mutableStateMapOf<String, Int>().also { map ->
                posts.forEach { map[it.id] = it.likeCount }
            }
        }

        Scaffold(
            /*topBar = { FeedTopBar() },*/
            containerColor = ColorBackground,
        ) { innerPadding ->
            FeedList(
                posts = posts,
                likedIds = likedIds,
                likeCounts = likeCounts,
                contentPadding = innerPadding,
                onLikeToggle = { post ->
                    val wasLiked = likedIds[post.id] == true
                    likedIds[post.id] = !wasLiked
                    likeCounts[post.id] = post.likeCount + if (!wasLiked) 1 else 0
                },
            )
        }
    }
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
            containerColor = ColorBackground,
            scrolledContainerColor = ColorBackground,
        ),
        windowInsets = WindowInsets.statusBars,
    )
}

// ─────────────────────────────────────────────
// Feed List
// ─────────────────────────────────────────────

@Composable
private fun FeedList(
    posts: List<Post>,
    likedIds: Map<String, Boolean>,
    likeCounts: Map<String, Int>,
    contentPadding: PaddingValues,
    onLikeToggle: (Post) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground),
        contentPadding = contentPadding,
    ) {
        items(
            items = posts,
            key = { it.id },
        ) { post ->
            PostItem(
                post = post,
                isLiked = likedIds[post.id] == true,
                displayLikeCount = likeCounts[post.id] ?: post.likeCount,
                onLikeToggle = { onLikeToggle(post) },
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
    displayLikeCount: Int,
    onLikeToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing16, vertical = Spacing12),
            horizontalArrangement = Arrangement.spacedBy(Spacing12),
        ) {
            // Avatar
            PostAvatar(avatarUrl = post.avatarUrl)

            // Content column
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing4),
            ) {
                PostHeader(
                    username = post.username,
                    timestamp = post.timestamp,
                )
                PostBody(
                    body = post.body,
                    imageUrl = post.imageUrl,
                )
                PostActions(
                    isLiked = isLiked,
                    likeCount = displayLikeCount,
                    commentCount = post.commentCount,
                    shareCount = post.shareCount,
                    onLikeToggle = onLikeToggle,
                )
            }
        }

        HorizontalDivider(
            color = ColorDivider,
            thickness = 0.5.dp,
        )
    }
}

// ─────────────────────────────────────────────
// Avatar
// ─────────────────────────────────────────────

@Composable
private fun PostAvatar(avatarUrl: String) {
    AsyncImage(
        model = avatarUrl,
        contentDescription = "Avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(ColorSurface),
    )
}

// ─────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────

@Composable
private fun PostHeader(
    username: String,
    timestamp: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing8),
    ) {
        Text(
            text = username,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = (-0.3).sp,
            modifier = Modifier.weight(1f, fill = false),
        )
        Text(
            text = "·",
            fontSize = 13.sp,
            color = ColorSecondaryText,
        )
        Text(
            text = timestamp,
            fontSize = 13.sp,
            color = ColorSecondaryText,
        )
    }
}

// ─────────────────────────────────────────────
// Body + Optional Image
// ─────────────────────────────────────────────

@Composable
private fun PostBody(
    body: String,
    imageUrl: String?,
) {
    Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        lineHeight = 22.sp,
    )

    if (imageUrl != null) {
        Spacer(modifier = Modifier.height(Spacing8))
        AsyncImage(
            model = imageUrl,
            contentDescription = "Post image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
                .background(ColorSurface),
        )
    }
}

// ─────────────────────────────────────────────
// Action Row
// ─────────────────────────────────────────────

@Composable
private fun PostActions(
    isLiked: Boolean,
    likeCount: Int,
    commentCount: Int,
    shareCount: Int,
    onLikeToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing4),
        horizontalArrangement = Arrangement.spacedBy(Spacing4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Like
        ActionButton(
            count = likeCount,
            contentDescription = if (isLiked) "Unlike" else "Like",
            tint = if (isLiked) ColorLike else ColorSecondaryText,
            icon = {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isLiked) ColorLike else ColorSecondaryText,
                    modifier = Modifier.size(20.dp),
                )
            },
            onClick = onLikeToggle,
        )

        // Comment
        ActionButton(
            count = commentCount,
            contentDescription = "Comment",
            tint = ColorSecondaryText,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = null,
                    tint = ColorSecondaryText,
                    modifier = Modifier.size(20.dp),
                )
            },
            onClick = {},
        )

        // Share
        ActionButton(
            count = shareCount,
            contentDescription = "Share",
            tint = ColorSecondaryText,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = null,
                    tint = ColorSecondaryText,
                    modifier = Modifier.size(20.dp),
                )
            },
            onClick = {},
        )
    }
}

@Composable
private fun ActionButton(
    count: Int,
    contentDescription: String,
    tint: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(end = Spacing8),
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp),
        ) {
            icon()
        }
        if (count > 0) {
            Text(
                text = formatCount(count),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = tint,
                letterSpacing = (-0.1).sp,
            )
        }
    }
}

// ─────────────────────────────────────────────
// Utilities
// ─────────────────────────────────────────────

private fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> "%.1fM".format(count / 1_000_000.0).trimEnd('0').trimEnd('.')
    count >= 1_000     -> "%.1fk".format(count / 1_000.0).trimEnd('0').trimEnd('.')
    else               -> count.toString()
}

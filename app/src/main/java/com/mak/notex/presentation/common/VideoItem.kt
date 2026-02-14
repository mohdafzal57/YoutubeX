package com.mak.notex.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mak.notex.domain.model.VideoFeed
import java.util.Locale

internal fun formatDuration(duration: Double): String {
    val seconds = duration.toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (minutes >= 60) {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        String.format("%d:%02d:%02d", hours, remainingMinutes, remainingSeconds)
    } else {
        String.format("%02d:%02d", minutes, remainingSeconds)
    }
}

private fun formatLikesCount(count: Int): String {
    return when {
        count >= 100_000_000 -> "${(count / 1_000_000)}M"
        count >= 100_000 -> String.format(Locale.US, "%.1f lakh", count / 100_000.0)
        count >= 1_000 -> String.format(Locale.US, "%.1fK", count / 1000.0)
        else -> count.toString()
    }
}


@Composable
fun VideoItem(
    video: VideoFeed,
    onClick: () -> Unit,
    onNavigateToChannel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(bottom = 16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Text(
                text = formatDuration(video.duration),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.avatar)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onNavigateToChannel),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${video.username} • ${formatLikesCount(video.likesCount)} views • 10 hours ago",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
            }
        }
    }
}
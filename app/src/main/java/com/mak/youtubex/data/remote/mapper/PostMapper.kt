package com.mak.youtubex.data.remote.mapper

import com.mak.youtubex.data.local.PostEntity
import com.mak.youtubex.data.remote.dto.post.PostDto
import com.mak.youtubex.presentation.main.social_feed.Post
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun PostDto.toDomain(): Post {
    return Post(
        id = id,
        avatarUrl = avatar,
        username = username,
        timestamp = formatSocialMediaTime(createdAt),
        body = content,
        imageUrls = images,
        likeCount = likesCount,
        commentCount = commentsCount,
        isLiked = isLiked
    )
}

fun PostDto.toEntity(currentTime: Long): PostEntity {
    return PostEntity(
        id = id,
        content = content,
        images = images,
        visibility = visibility,
        deletedAt = deletedAt,
        likesCount = likesCount,
        commentsCount = commentsCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        authorId = authorId,
        username = username,
        fullName = fullName,
        avatar = avatar,
        isLiked = isLiked,
        lastUpdated = currentTime
    )
}


fun PostEntity.toDomain(): Post {
    return Post(
        id = id,
        avatarUrl = avatar,
        username = username,
        timestamp = formatSocialMediaTime(createdAt),
        body = content,
        imageUrls = images,
        likeCount = likesCount,
        commentCount = commentsCount,
        isLiked = isLiked
    )
}

fun formatSocialMediaTime(isoString: String): String {
    return try {
        val postTime = Instant.parse(isoString)
        val now = Instant.now()

        val minutes = ChronoUnit.MINUTES.between(postTime, now)
        val hours = ChronoUnit.HOURS.between(postTime, now)
        val days = ChronoUnit.DAYS.between(postTime, now)

        when {
            minutes < 1 -> "now"
            minutes < 60 -> "${minutes}m"
            hours < 24 -> "${hours}h"
            days < 7 -> "${days}d"
            else -> {
                // For posts older than a week, show "Mar 26" or "Dec 5"
                val formatter = DateTimeFormatter.ofPattern("MMM d")
                    .withZone(ZoneId.systemDefault())
                formatter.format(postTime)
            }
        }
    } catch (e: Exception) {
        "" // Fallback just in case the string is malformed
    }
}
package com.mak.notex.data.remote.mapper

import com.mak.notex.data.remote.dto.video.VideoDto
import com.mak.notex.data.remote.dto.video.VideoFeedDto
import com.mak.notex.domain.model.Video
import com.mak.notex.domain.model.VideoFeed

fun VideoDto.toDomain(): Video {
    return Video(
        id = id,
        videoFile = videoFile,
        thumbnail = thumbnail,
        title = title,
        description = description,
        duration = duration,
        views = views,
        ownerId = ownerId,
        createdAt = createdAt
    )
}

fun VideoFeedDto.toDomain(): VideoFeed {
    return VideoFeed(
        id = id,
        videoFile = videoFile,
        thumbnail = thumbnail,
        title = title,
        description = description,
        duration = duration,
        views = views,
        createdAt = createdAt,
        updatedAt = updatedAt,
        likesCount = likesCount,
        isLikedByMe = isLikedByMe,
        username = username,
        avatar = avatar,
        ownerId = ownerId
    )
}


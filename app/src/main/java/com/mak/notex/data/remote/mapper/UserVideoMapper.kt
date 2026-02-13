package com.mak.notex.data.remote.mapper

import com.mak.notex.data.remote.dto.video.UserVideoDto
import com.mak.notex.domain.model.UserVideo

fun UserVideoDto.toDomain(): UserVideo {
    return UserVideo(
        id = id,
        videoFile = videoFile,
        thumbnail = thumbnail,
        title = title,
        description = description,
        duration = duration,
        views = views,
        isPublished = isPublished,
        createdAt = createdAt,
        updatedAt = updatedAt,
        username = username,
        avatar = avatar
    )
}

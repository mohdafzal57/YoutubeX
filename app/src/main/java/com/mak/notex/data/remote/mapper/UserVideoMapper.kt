package com.mak.notex.data.remote.mapper

import com.mak.notex.data.remote.dto.video.UserVideoDto
import com.mak.notex.domain.model.UserVideo
import com.mak.notex.utils.formatDate
import com.mak.notex.utils.formatDuration

fun UserVideoDto.toDomain(): UserVideo {
    return UserVideo(
        id = id,
        videoFile = videoFile,
        thumbnail = thumbnail,
        title = title,
        description = description,
        duration = formatDuration(duration),
        views = views,
        isPublished = isPublished,
        createdAt = formatDate(createdAt),
        updatedAt = formatDate(updatedAt),
        username = username,
        avatar = avatar
    )
}

package com.mak.youtubex.data.remote.mapper

import com.mak.youtubex.data.remote.dto.video.UserVideoDto
import com.mak.youtubex.domain.model.UserVideo
import com.mak.youtubex.utils.formatDate
import com.mak.youtubex.utils.formatDuration

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

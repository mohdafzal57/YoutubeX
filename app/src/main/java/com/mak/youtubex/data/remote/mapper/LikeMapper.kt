package com.mak.youtubex.data.remote.mapper

import com.mak.youtubex.data.remote.dto.like.LikeDto
import com.mak.youtubex.domain.model.Like

fun LikeDto.toDomain(): Like {
    return Like(
        id = id,
        user = user,
        targetId = targetId,
        targetModel = targetModel,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

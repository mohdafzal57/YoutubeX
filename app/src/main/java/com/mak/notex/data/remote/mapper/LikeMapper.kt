package com.mak.notex.data.remote.mapper

import com.mak.notex.data.remote.dto.like.LikeDto
import com.mak.notex.domain.model.Like

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

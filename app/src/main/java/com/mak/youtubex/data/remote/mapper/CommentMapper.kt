package com.mak.youtubex.data.remote.mapper

import com.mak.youtubex.data.remote.dto.comment.CommentDto
import com.mak.youtubex.data.remote.dto.comment.CommentUserDto
import com.mak.youtubex.domain.model.Comment
import com.mak.youtubex.domain.model.CommentUser

fun CommentDto.toDomain(): Comment {
    return Comment(
        id = id,
        content = content,
        user = user.toDomain(),
        likesCount = likesCount,
        repliesCount = repliesCount,
        createdAt = createdAt
    )
}

fun CommentUserDto.toDomain(): CommentUser {
    return CommentUser(
        id = id,
        username = username,
        avatar = avatar
    )
}
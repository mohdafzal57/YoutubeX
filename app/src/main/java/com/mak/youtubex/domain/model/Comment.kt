package com.mak.youtubex.domain.model

data class Comment(
    val id: String,
    val content: String,
    val user: CommentUser,
    val likesCount: Int,
    val repliesCount: Int,
    val createdAt: String
)

data class CommentUser(
    val id: String,
    val username: String,
    val avatar: String
)
package com.mak.notex.domain.model

data class VideoFeed(
    val id: String,
    val videoFile: String,
    val thumbnail: String,
    val title: String,
    val description: String,
    val duration: Double,
    val views: Int,
    val createdAt: String,
    val updatedAt: String,
    val likesCount: Int,
    val isLikedByMe: Boolean,
    val username: String,
    val avatar: String,
    val ownerId: String,
)



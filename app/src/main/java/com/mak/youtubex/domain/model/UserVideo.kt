package com.mak.youtubex.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserVideo(
    val id: String,
    val videoFile: String,
    val thumbnail: String,
    val title: String,
    val description: String,
    val duration: String,
    val views: Int,
    val isPublished: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val username: String,
    val avatar: String
)

data class UserVideoRequest(
    val userId: String,
    val query: String = "",
    val sortBy: String = "createdAt",
    val sortType: String = "desc",
)
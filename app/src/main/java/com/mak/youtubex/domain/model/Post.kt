package com.mak.youtubex.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Post(
    val id: String,
    val avatarUrl: String,
    val username: String,
    val timestamp: String,
    val body: String,
    val imageUrls: List<String> = emptyList(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false
)

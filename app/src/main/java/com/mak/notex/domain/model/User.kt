package com.mak.notex.domain.model

data class User(
    val id: String,
    val avatar: String,
    val coverImage: String?,
    val createdAt: String,
    val email: String,
    val fullName: String,
    val updatedAt: String,
    val username: String,
    val recentWatched: List<RecentWatched>
)

data class RecentWatched(
    val id: String,
    val videoId: String,
    val watchedAt: String
)
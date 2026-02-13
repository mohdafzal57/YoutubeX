package com.mak.notex.domain.model

data class UserChannel(
    val id: String,
    val fullName: String,
    val username: String,
    val email: String,
    val avatar: String,
    val coverImage: String?,
    val subscribersCount: Int,
    val subscribeToCount: Int,
    val isUserSubscribed: Boolean
)

data class WatchHistoryItem(
    val id: String,
    val videoId: String,
    val title: String,
    val thumbnail: String,
    val duration: Double,
    val watchDuration: Int,
    val watchedAt: String,
    val progress: Float
)


data class Owner(
    val id: String,
    val fullName: String,
    val username: String,
    val avatar: String
)

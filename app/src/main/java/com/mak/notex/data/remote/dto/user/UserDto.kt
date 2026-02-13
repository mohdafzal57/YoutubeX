package com.mak.notex.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("_id")
    val id: String,
    val fullName: String,
    val username: String,
    val avatar: String,
    val coverImage: String?,
    val email: String,
    val updatedAt: String,
    val createdAt: String,
    val recentWatched: List<RecentWatchedDto> = emptyList()
)

data class RecentWatchedDto(
    @SerializedName("_id")
    val id: String,
    val videoId: String,
    val watchedAt: String
)

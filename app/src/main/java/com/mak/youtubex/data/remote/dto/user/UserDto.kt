package com.mak.youtubex.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("_id")
    val id: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("coverImage")
    val coverImage: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("recentWatched")
    val recentWatched: List<RecentWatchedDto> = emptyList()
)

data class RecentWatchedDto(
    @SerializedName("_id")
    val id: String,
    @SerializedName("videoId")
    val videoId: String,
    @SerializedName("watchedAt")
    val watchedAt: String
)

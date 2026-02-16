package com.mak.notex.data.remote.dto.user

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class UserChannelDto(
    @SerializedName("_id")
    val id: String,
    val avatar: String,
    val coverImage: String,
    val email: String,
    val fullName: String,
    val isUserSubscribed: Boolean,
    val subscribeToCount: Int,
    val subscribersCount: Int,
    val videosCount: Int,
    val username: String
)
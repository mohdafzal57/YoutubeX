package com.mak.notex.data.remote.dto.tweet

import com.google.gson.annotations.SerializedName

data class TweetResponseDto(
    @SerializedName("__v")
    val v: Int,
    @SerializedName("_id")
    val id: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val user: String
)
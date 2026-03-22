package com.mak.youtubex.data.remote.dto.video

import com.google.gson.annotations.SerializedName

data class UserVideoDto(
    @SerializedName("_id")
    val id: String,
    val videoFile: String,
    val thumbnail: String,
    val title: String,
    val description: String,
    val duration: Double,
    val views: Int,
    val isPublished: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val username: String,
    val avatar: String
)
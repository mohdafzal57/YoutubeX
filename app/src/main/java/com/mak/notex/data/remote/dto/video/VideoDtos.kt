package com.mak.notex.data.remote.dto.video

import com.google.gson.annotations.SerializedName

data class VideoDto(
    @SerializedName("_id") val id: String,
    val videoFile: String,
    val thumbnail: String,
    val title: String,
    val description: String,
    val duration: Double,
    val views: Int,
    val isPublished: Boolean,
    @SerializedName("owner") val ownerId: String,
    val createdAt: String,
    val updatedAt: String,
    @SerializedName("__v") val v: Int,
)


data class VideoFeedDto(
    @SerializedName("_id") val id: String,
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


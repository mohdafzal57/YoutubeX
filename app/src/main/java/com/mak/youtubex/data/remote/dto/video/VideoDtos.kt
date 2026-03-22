package com.mak.youtubex.data.remote.dto.video

import com.google.gson.annotations.SerializedName

data class VideoDto(
    @SerializedName("_id") val id: String,
    @SerializedName("videoFile") val videoFile: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("duration") val duration: Double,
    @SerializedName("views") val views: Int,
    @SerializedName("isPublished") val isPublished: Boolean,
    @SerializedName("owner") val ownerId: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("__v") val v: Int,
)


data class VideoFeedDto(
    @SerializedName("_id") val id: String,
    @SerializedName("videoFile") val videoFile: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("duration") val duration: Double,
    @SerializedName("views") val views: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("likesCount") val likesCount: Int,
    @SerializedName("isLikedByMe") val isLikedByMe: Boolean,
    @SerializedName("username") val username: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("ownerId") val ownerId: String,
)

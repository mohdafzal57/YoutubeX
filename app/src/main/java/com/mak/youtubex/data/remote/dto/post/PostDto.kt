package com.mak.youtubex.data.remote.dto.post

import com.google.gson.annotations.SerializedName

data class PostDto(

    @SerializedName("_id")
    val id: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("images")
    val images: List<String> = emptyList(),

    @SerializedName("visibility")
    val visibility: String,

    @SerializedName("deletedAt")
    val deletedAt: String?,

    @SerializedName("likesCount")
    val likesCount: Int,

    @SerializedName("commentsCount")
    val commentsCount: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("authorId")
    val authorId: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("avatar")
    val avatar: String,

    @SerializedName("isLiked")
    val isLiked: Boolean = false
)
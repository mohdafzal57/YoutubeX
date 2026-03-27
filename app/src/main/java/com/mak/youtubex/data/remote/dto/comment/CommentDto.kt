package com.mak.youtubex.data.remote.dto.comment

import com.google.gson.annotations.SerializedName
import com.mak.youtubex.data.remote.dto.user.UserDto

data class CommentDto(
    @SerializedName("_id")
    val id: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("user")
    val user: CommentUserDto,

    @SerializedName("likesCount")
    val likesCount: Int,

    @SerializedName("repliesCount")
    val repliesCount: Int,

    @SerializedName("createdAt")
    val createdAt: String
)

data class CommentUserDto(
    @SerializedName("_id")
    val id: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("avatar")
    val avatar: String
)
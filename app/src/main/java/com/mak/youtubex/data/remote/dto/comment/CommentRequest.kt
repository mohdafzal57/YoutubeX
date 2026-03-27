package com.mak.youtubex.data.remote.dto.comment

import com.google.gson.annotations.SerializedName

data class CommentRequest(
    @SerializedName("content")
    val content: String
)
package com.mak.notex.data.remote.dto.like

import com.google.gson.annotations.SerializedName

data class LikeDto(
    @SerializedName("_id")
    val id: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("targetId")
    val targetId: String,
    @SerializedName("targetModel")
    val targetModel: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)
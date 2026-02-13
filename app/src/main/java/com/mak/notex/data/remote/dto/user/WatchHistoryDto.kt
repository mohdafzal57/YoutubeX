package com.mak.notex.data.remote.dto.user

import kotlinx.serialization.SerialName

data class WatchHistoryDto(
    @SerialName("_id")
    val id: String,
    val videoId: String,
    val title: String,
    val thumbnail: String,
    val duration: Double,
    val watchDuration: Int,
    val watchedAt: String
)

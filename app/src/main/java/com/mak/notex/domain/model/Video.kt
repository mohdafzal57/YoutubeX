package com.mak.notex.domain.model

import android.net.Uri

data class VideoUploadRequest(
    val videoFile: Uri,
    val thumbnail: Uri,
    val title: String,
    val description: String
)

data class Video(
    val id: String,
    val videoFile: String,
    val thumbnail: String,
    val title: String,
    val description: String,
    val duration: Double,
    val views: Int,
    val ownerId: String,
    val createdAt: String,
)

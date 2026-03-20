package com.mak.notex.domain.model

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class LocalVideo(
    val id: Long,
    val uri: Uri,
    val name: String,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val folderId: Long,
    val folderName: String
)

@Immutable
data class VideoFolder(
    val id: Long,
    val name: String,
    val thumbnailUri: Uri, // first video as a thumbnail for the folder
    val videoCount: Int
)

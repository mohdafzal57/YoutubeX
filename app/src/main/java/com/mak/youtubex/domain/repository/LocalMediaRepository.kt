package com.mak.youtubex.domain.repository

import com.mak.youtubex.domain.model.LocalVideo
import com.mak.youtubex.domain.model.VideoFolder
import kotlinx.coroutines.flow.Flow

interface LocalMediaRepository {
    fun getLocalVideos(folderId: Long? = null): Flow<List<LocalVideo>>
    fun getVideoFolders(): Flow<List<VideoFolder>>
}

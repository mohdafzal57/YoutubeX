package com.mak.notex.domain.repository

import com.mak.notex.domain.model.LocalVideo
import com.mak.notex.domain.model.VideoFolder
import kotlinx.coroutines.flow.Flow

interface LocalMediaRepository {
    fun getLocalVideos(folderId: Long? = null): Flow<List<LocalVideo>>
    fun getVideoFolders(): Flow<List<VideoFolder>>
}

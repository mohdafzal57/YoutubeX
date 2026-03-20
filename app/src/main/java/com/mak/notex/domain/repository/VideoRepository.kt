package com.mak.notex.domain.repository

import androidx.paging.PagingData
import com.mak.notex.domain.model.UserVideo
import com.mak.notex.domain.model.UserVideoRequest
import com.mak.notex.domain.model.VideoFeed
import com.mak.notex.domain.model.VideoUploadRequest
import com.mak.notex.core.data.util.NetworkError
import com.mak.notex.core.data.util.Result
import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    suspend fun uploadVideo(request: VideoUploadRequest): Result<Unit, NetworkError>

    fun getUserVideos(
        userVideoRequest: UserVideoRequest
    ): Flow<PagingData<UserVideo>>

    fun getVideoFeed(query: String? = null): Flow<PagingData<VideoFeed>>
}

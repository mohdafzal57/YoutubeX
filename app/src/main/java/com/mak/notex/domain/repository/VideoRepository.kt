package com.mak.notex.domain.repository

import androidx.paging.PagingData
import com.mak.notex.domain.model.UserVideo
import com.mak.notex.domain.model.UserVideoRequest
import com.mak.notex.domain.model.Video
import com.mak.notex.domain.model.VideoFeed
import com.mak.notex.domain.model.VideoUploadRequest
import com.mak.notex.utils.NetworkError
import com.mak.notex.utils.Result
import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    suspend fun uploadVideo(request: VideoUploadRequest): Result<Video, NetworkError>

    fun getUserVideos(
        userVideoRequest: UserVideoRequest
    ): Flow<PagingData<UserVideo>>

    fun getVideoFeed(query: String? = null): Flow<PagingData<VideoFeed>>
}

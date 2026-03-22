package com.mak.youtubex.domain.repository

import androidx.paging.PagingData
import com.mak.youtubex.domain.model.UserVideo
import com.mak.youtubex.domain.model.UserVideoRequest
import com.mak.youtubex.domain.model.VideoFeed
import com.mak.youtubex.domain.model.VideoUploadRequest
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.Result
import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    suspend fun uploadVideo(request: VideoUploadRequest): Result<Unit, NetworkError>

    fun getUserVideos(
        userVideoRequest: UserVideoRequest
    ): Flow<PagingData<UserVideo>>

    fun getVideoFeed(query: String? = null): Flow<PagingData<VideoFeed>>
}

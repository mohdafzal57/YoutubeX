package com.mak.notex.data.repository

import android.content.ContentResolver
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mak.notex.data.paging.UserVideosPagingSource
import com.mak.notex.data.paging.VideoFeedPagingSource
import com.mak.notex.data.remote.api.VideoApi
import com.mak.notex.data.remote.mapper.toCompressedMultipart
import com.mak.notex.data.remote.mapper.toDomain
import com.mak.notex.data.remote.mapper.toTextRequestBody
import com.mak.notex.data.remote.mapper.toVideoMultipart
import com.mak.notex.domain.model.UserVideo
import com.mak.notex.domain.model.UserVideoRequest
import com.mak.notex.domain.model.Video
import com.mak.notex.domain.model.VideoFeed
import com.mak.notex.domain.model.VideoUploadRequest
import com.mak.notex.domain.repository.VideoRepository
import com.mak.notex.utils.NetworkError
import com.mak.notex.utils.Result
import com.mak.notex.utils.map
import com.mak.notex.utils.safeCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val videoApi: VideoApi,
    private val contentResolver: ContentResolver
) : VideoRepository {

    override suspend fun uploadVideo(
        request: VideoUploadRequest
    ): Result<Video, NetworkError> {
        return safeCall {
            videoApi.publishAVideo(
                videoFile = request.videoFile.toVideoMultipart(contentResolver, "videoFile"),
                thumbnail = request.thumbnail.toCompressedMultipart(contentResolver, "thumbnail"),
                title = request.title.toTextRequestBody(),
                description = request.description.toTextRequestBody()
            )
        }.map { it.toDomain() }
    }

    override fun getUserVideos(
        userVideoRequest: UserVideoRequest
    ): Flow<PagingData<UserVideo>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UserVideosPagingSource(
                    videoApi = videoApi,
                    userVideoRequest = userVideoRequest
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun getVideoFeed(query: String?): Flow<PagingData<VideoFeed>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                VideoFeedPagingSource(
                    query = query,
                    api = videoApi
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 5
    }
}

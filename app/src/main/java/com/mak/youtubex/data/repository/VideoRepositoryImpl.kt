package com.mak.youtubex.data.repository

import android.content.ContentResolver
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mak.youtubex.data.paging.UserVideosPagingSource
import com.mak.youtubex.data.paging.VideoFeedPagingSource
import com.mak.youtubex.data.remote.api.VideoApi
import com.mak.youtubex.data.remote.mapper.toCompressedMultipart
import com.mak.youtubex.data.remote.mapper.toDomain
import com.mak.youtubex.data.remote.mapper.toTextRequestBody
import com.mak.youtubex.data.remote.mapper.toVideoMultipart
import com.mak.youtubex.domain.model.UserVideo
import com.mak.youtubex.domain.model.UserVideoRequest
import com.mak.youtubex.domain.model.VideoFeed
import com.mak.youtubex.domain.model.VideoUploadRequest
import com.mak.youtubex.domain.repository.VideoRepository
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.Result
import com.mak.youtubex.core.data.util.safeCall
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
    ): Result<Unit, NetworkError> {
        return safeCall {
            videoApi.publishAVideo(
                videoFile = request.videoFile.toVideoMultipart(contentResolver, "videoFile"),
                thumbnail = request.thumbnail.toCompressedMultipart(contentResolver, "thumbnail"),
                title = request.title.toTextRequestBody(),
                description = request.description.toTextRequestBody()
            )
        }
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

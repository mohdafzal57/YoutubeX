package com.mak.youtubex.data.repository

import com.mak.youtubex.data.remote.api.LikeApi
import com.mak.youtubex.data.remote.mapper.toDomain
import com.mak.youtubex.domain.model.Like
import com.mak.youtubex.domain.model.Video
import com.mak.youtubex.domain.repository.LikeRepository
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.map
import com.mak.youtubex.core.data.util.safeCall
import javax.inject.Inject
import javax.inject.Singleton
import com.mak.youtubex.core.data.util.Result
import kotlin.collections.map

@Singleton
class LikeRepositoryImpl @Inject constructor(
    private val likeApi: LikeApi
) : LikeRepository {

    override suspend fun toggleVideoLike(videoId: String): Result<Like, NetworkError> =
        safeCall {
            likeApi.toggleVideoLike(videoId)
        }.map { it.toDomain() }

    override suspend fun toggleCommentLike(commentId: String): Result<Like, NetworkError> =
        safeCall {
            likeApi.toggleCommentLike(commentId)
        }.map { it.toDomain() }

    override suspend fun toggleTweetLike(tweetId: String): Result<Like, NetworkError> =
        safeCall {
            likeApi.toggleTweetLike(tweetId)
        }.map { it.toDomain() }

    override suspend fun getLikedVideos(): Result<List<Video>, NetworkError> =
        safeCall {
            likeApi.getLikedVideos()
        }.map { list ->
            list.map { it.toDomain() }
        }
}


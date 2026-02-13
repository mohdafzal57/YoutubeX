package com.mak.notex.data.repository

import com.mak.notex.data.remote.api.LikeApi
import com.mak.notex.data.remote.mapper.toDomain
import com.mak.notex.domain.model.Like
import com.mak.notex.domain.model.Video
import com.mak.notex.domain.repository.LikeRepository
import com.mak.notex.utils.NetworkError
import com.mak.notex.utils.map
import com.mak.notex.utils.safeCall
import javax.inject.Inject
import javax.inject.Singleton
import com.mak.notex.utils.Result
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


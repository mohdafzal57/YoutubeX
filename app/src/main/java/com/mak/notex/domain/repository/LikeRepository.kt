package com.mak.notex.domain.repository

import com.mak.notex.domain.model.Like
import com.mak.notex.domain.model.Video
import com.mak.notex.core.data.util.NetworkError
import com.mak.notex.core.data.util.Result

interface LikeRepository {

    suspend fun toggleVideoLike(videoId: String): Result<Like, NetworkError>

    suspend fun toggleCommentLike(commentId: String): Result<Like, NetworkError>

    suspend fun toggleTweetLike(tweetId: String): Result<Like, NetworkError>

    suspend fun getLikedVideos(): Result<List<Video>, NetworkError>
}

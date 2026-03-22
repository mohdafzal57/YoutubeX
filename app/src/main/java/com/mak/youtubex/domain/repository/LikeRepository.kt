package com.mak.youtubex.domain.repository

import com.mak.youtubex.domain.model.Like
import com.mak.youtubex.domain.model.Video
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.Result

interface LikeRepository {

    suspend fun toggleVideoLike(videoId: String): Result<Like, NetworkError>

    suspend fun toggleCommentLike(commentId: String): Result<Like, NetworkError>

    suspend fun toggleTweetLike(tweetId: String): Result<Like, NetworkError>

    suspend fun getLikedVideos(): Result<List<Video>, NetworkError>
}

package com.mak.notex.data.remote.api

import com.mak.notex.data.remote.dto.ApiResponse
import com.mak.notex.data.remote.dto.like.LikeDto
import com.mak.notex.data.remote.dto.video.VideoDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LikeApi {

    @POST("likes/toggle/v/{videoId}")
    suspend fun toggleVideoLike(
        @Path("videoId") videoId: String
    ): Response<ApiResponse<LikeDto>>

    @POST("likes/toggle/c/{commentId}")
    suspend fun toggleCommentLike(
        @Path("commentId") commentId: String
    ): Response<ApiResponse<LikeDto>>

    @POST("likes/toggle/t/{tweetId}")
    suspend fun toggleTweetLike(
        @Path("tweetId") tweetId: String
    ): Response<ApiResponse<LikeDto>>

    @GET("likes/videos")
    suspend fun getLikedVideos(): Response<ApiResponse<List<VideoDto>>>
}

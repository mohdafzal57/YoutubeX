package com.mak.youtubex.data.remote.api

import com.mak.youtubex.data.remote.dto.ApiResponse
import com.mak.youtubex.data.remote.dto.PaginatedResponseDto
import com.mak.youtubex.data.remote.dto.video.UserVideoDto
import com.mak.youtubex.data.remote.dto.video.VideoFeedDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface VideoApi {

    @GET("videos")
    suspend fun getAllVideosOfAUser(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("query") query: String,
        @Query("sortBy") sortBy: String,
        @Query("sortType") sortType: String,
        @Query("userId") userId: String,
    ): Response<ApiResponse<PaginatedResponseDto<UserVideoDto>>>

    @Multipart
    @POST("videos")
    suspend fun publishAVideo(
        @Part videoFile: MultipartBody.Part,
        @Part thumbnail: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody
    ): Response<ApiResponse<Unit>>

    @GET("videos/feed")
    suspend fun getVideoFeed(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("query") query: String?
    ): Response<ApiResponse<PaginatedResponseDto<VideoFeedDto>>>
}

package com.mak.youtubex.data.remote.api

import com.mak.youtubex.data.remote.dto.ApiResponse
import com.mak.youtubex.data.remote.dto.tweet.CreateTweetRequest
import com.mak.youtubex.data.remote.dto.tweet.TweetResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TweetApi {

    @POST("tweets")
    suspend fun createTweet(
        @Body request: CreateTweetRequest
    ): Response<ApiResponse<TweetResponseDto>>
}
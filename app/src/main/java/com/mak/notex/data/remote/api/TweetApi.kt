package com.mak.notex.data.remote.api

import com.mak.notex.data.remote.dto.ApiResponse
import com.mak.notex.data.remote.dto.tweet.CreateTweetRequest
import com.mak.notex.data.remote.dto.tweet.TweetResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TweetApi {

    @POST("tweets")
    suspend fun createTweet(
        @Body request: CreateTweetRequest
    ): Response<ApiResponse<TweetResponseDto>>
}
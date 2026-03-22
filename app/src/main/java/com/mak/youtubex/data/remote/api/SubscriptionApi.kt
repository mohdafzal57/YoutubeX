package com.mak.youtubex.data.remote.api

import com.mak.youtubex.data.remote.dto.ApiResponse
import com.mak.youtubex.data.remote.dto.subscription.SubscriptionDto
import com.mak.youtubex.data.remote.dto.subscription.SubscriptionProfileDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SubscriptionApi {

    @POST("subscriptions/c/{channelId}")
    suspend fun toggleSubscription(
        @Path("channelId") channelId: String
    ): Response<ApiResponse<SubscriptionDto>>

    @GET("subscriptions/c/{channelId}/subscribers")
    suspend fun getChannelSubscribers(
        @Path("channelId") channelId: String
    ): Response<ApiResponse<List<SubscriptionProfileDto>>>

    @GET("subscriptions/u/{userId}/subscriptions")
    suspend fun getUserSubscribedChannels(
        @Path("userId") userId: String
    ): Response<ApiResponse<List<SubscriptionProfileDto>>>
}

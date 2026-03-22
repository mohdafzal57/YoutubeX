package com.mak.youtubex.data.remote.dto.subscription

import com.google.gson.annotations.SerializedName

data class SubscriptionProfile(
    @SerializedName("id") val id: String,
    @SerializedName("name") val username: String,
    @SerializedName("avatar") val avatarUrl: String
)

data class Subscription(
    @SerializedName("_id") val id: String,
    @SerializedName("subscriber") val subscriberId: String,
    @SerializedName("channel") val channelId: String,
    @SerializedName("createdAt") val createdAt: String
)
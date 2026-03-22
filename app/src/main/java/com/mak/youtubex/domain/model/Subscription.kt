package com.mak.youtubex.domain.model

data class SubscriptionProfile(
    val id: String,
    val username: String,
    val avatarUrl: String
)

data class Subscription(
    val id: String,
    val subscriberId: String,
    val channelId: String,
    val createdAt: String
)

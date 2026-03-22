package com.mak.youtubex.data.remote.mapper

import com.mak.youtubex.data.remote.dto.subscription.SubscriptionDto
import com.mak.youtubex.data.remote.dto.subscription.SubscriptionProfileDto
import com.mak.youtubex.domain.model.Subscription
import com.mak.youtubex.domain.model.SubscriptionProfile

fun SubscriptionDto.toDomain(): Subscription {
    return Subscription(
        id = id,
        subscriberId = subscriberId,
        channelId = channelId,
        createdAt = createdAt
    )
}

fun SubscriptionProfileDto.toDomain(): SubscriptionProfile {
    return SubscriptionProfile(
        id = id,
        username = username,
        avatarUrl = avatarUrl
    )
}

package com.mak.youtubex.domain.repository

import com.mak.youtubex.domain.model.Subscription
import com.mak.youtubex.domain.model.SubscriptionProfile
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.Result

interface SubscriptionRepository {

    suspend fun toggleSubscription(
        channelId: String
    ): Result<Subscription, NetworkError>

    suspend fun getChannelSubscribers(
        channelId: String
    ): Result<List<SubscriptionProfile>, NetworkError>

    suspend fun getUserSubscribedChannels():
            Result<List<SubscriptionProfile>, NetworkError>
}

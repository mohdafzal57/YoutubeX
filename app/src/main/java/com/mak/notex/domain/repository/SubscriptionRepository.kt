package com.mak.notex.domain.repository

import com.mak.notex.domain.model.Subscription
import com.mak.notex.domain.model.SubscriptionProfile
import com.mak.notex.utils.NetworkError
import com.mak.notex.utils.Result

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

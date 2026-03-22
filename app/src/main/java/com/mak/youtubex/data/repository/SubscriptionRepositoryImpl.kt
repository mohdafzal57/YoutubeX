package com.mak.youtubex.data.repository

import com.mak.youtubex.core.datastore.TokenManager
import com.mak.youtubex.data.remote.api.SubscriptionApi
import com.mak.youtubex.data.remote.mapper.toDomain
import com.mak.youtubex.domain.model.Subscription
import com.mak.youtubex.domain.model.SubscriptionProfile
import com.mak.youtubex.domain.repository.SubscriptionRepository
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.Result
import com.mak.youtubex.core.data.util.map
import com.mak.youtubex.core.data.util.safeCall
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager,
    private val api: SubscriptionApi
) : SubscriptionRepository {

    override suspend fun toggleSubscription(
        channelId: String
    ): Result<Subscription, NetworkError> {
        return safeCall {
            api.toggleSubscription(channelId)
        }.map { it.toDomain() }
    }

    override suspend fun getChannelSubscribers(
        channelId: String
    ): Result<List<SubscriptionProfile>, NetworkError> {
        return safeCall {
            api.getChannelSubscribers(channelId)
        }.map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getUserSubscribedChannels():
            Result<List<SubscriptionProfile>, NetworkError> {
        val userId =
            tokenManager.getUserId().first() ?: return Result.Error(NetworkError.UNAUTHORIZED)
        return safeCall {
            api.getUserSubscribedChannels(userId)
        }.map { list ->
            list.map { it.toDomain() }
        }
    }
}

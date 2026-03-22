package com.mak.youtubex.di

import com.mak.youtubex.data.repository.LikeRepositoryImpl
import com.mak.youtubex.data.repository.LocalMediaRepositoryImpl
import com.mak.youtubex.data.repository.SubscriptionRepositoryImpl
import com.mak.youtubex.data.repository.UserRepositoryImpl
import com.mak.youtubex.data.repository.VideoRepositoryImpl
import com.mak.youtubex.data.utils.ConnectivityManagerNetworkMonitor
import com.mak.youtubex.data.utils.NetworkMonitor
import com.mak.youtubex.domain.repository.LikeRepository
import com.mak.youtubex.domain.repository.LocalMediaRepository
import com.mak.youtubex.domain.repository.SubscriptionRepository
import com.mak.youtubex.domain.repository.UserRepository
import com.mak.youtubex.domain.repository.VideoRepository
import com.mak.youtubex.utils.CoilMediaPrefetcher
import com.mak.youtubex.utils.MediaPrefetcher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindLikeRepository(
        impl: LikeRepositoryImpl
    ): LikeRepository

    @Binds
    @Singleton
    abstract fun bindVideoRepository(
        impl: VideoRepositoryImpl
    ): VideoRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        impl: SubscriptionRepositoryImpl
    ): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindLocalMediaRepository(
        impl: LocalMediaRepositoryImpl
    ): LocalMediaRepository

    @Binds
    @Singleton
    abstract fun bindMediaPrefetcher(
        impl: CoilMediaPrefetcher
    ): MediaPrefetcher
}

package com.mak.notex.di

import com.mak.notex.data.repository.LikeRepositoryImpl
import com.mak.notex.data.repository.LocalMediaRepositoryImpl
import com.mak.notex.data.repository.SubscriptionRepositoryImpl
import com.mak.notex.data.repository.UserRepositoryImpl
import com.mak.notex.data.repository.VideoRepositoryImpl
import com.mak.notex.data.utils.ConnectivityManagerNetworkMonitor
import com.mak.notex.data.utils.NetworkMonitor
import com.mak.notex.domain.repository.LikeRepository
import com.mak.notex.domain.repository.LocalMediaRepository
import com.mak.notex.domain.repository.SubscriptionRepository
import com.mak.notex.domain.repository.UserRepository
import com.mak.notex.domain.repository.VideoRepository
import com.mak.notex.utils.CoilMediaPrefetcher
import com.mak.notex.utils.MediaPrefetcher
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

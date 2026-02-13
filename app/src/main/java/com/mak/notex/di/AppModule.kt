package com.mak.notex.di

import com.mak.notex.BuildConfig
import com.mak.notex.core.di.AuthenticatedClient
import com.mak.notex.core.di.PublicClient
import com.mak.notex.core.di.TokenRefreshClient
import com.mak.notex.data.remote.api.LikeApi
import com.mak.notex.data.remote.api.RefreshTokenApi
import com.mak.notex.data.remote.api.SubscriptionApi
import com.mak.notex.data.remote.api.TweetApi
import com.mak.notex.data.remote.api.UserApi
import com.mak.notex.data.remote.api.VideoApi
import com.mak.notex.core.network.auth.AuthAuthenticator
import com.mak.notex.core.network.interceptor.AccessTokenInterceptor
import com.mak.notex.core.network.interceptor.RefreshTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlin.jvm.java

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @[Singleton Provides AuthenticatedClient]
    fun provideAccessOkHttpClient(
        accessTokenInterceptor: AccessTokenInterceptor,
        authAuthenticator: AuthAuthenticator
        ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(authAuthenticator) //  the AuthAuthenticator automatically takes over to refresh the token and then resends the failed request.
            .addInterceptor(accessTokenInterceptor)
            .build()
    }

    /***
        Refresh calls must NOT trigger token refresh
        Otherwise → infinite 401 loop
    ***/
    @[Singleton Provides TokenRefreshClient]
    fun provideRefreshOkHttpClient(
        refreshTokenInterceptor: RefreshTokenInterceptor,
        ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(refreshTokenInterceptor)
            .build()
    }

    @[Singleton Provides PublicClient]
    fun provideUnAuthenticatedOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    // Api's starting from here!!
    @Singleton
    @Provides
    fun provideUserApi(
        retrofitBuilder: Retrofit.Builder,
        @AuthenticatedClient okHttpClient: OkHttpClient
    ): UserApi {
        return retrofitBuilder.client(okHttpClient).build().create(UserApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTweetApi(
        retrofitBuilder: Retrofit.Builder,
        @AuthenticatedClient okHttpClient: OkHttpClient
    ): TweetApi {
        return retrofitBuilder.client(okHttpClient).build().create(TweetApi::class.java)
    }

    @Singleton
    @Provides
    fun provideVideoApi(
        retrofitBuilder: Retrofit.Builder,
        @AuthenticatedClient okHttpClient: OkHttpClient
    ): VideoApi {
        return retrofitBuilder.client(okHttpClient).build().create(VideoApi::class.java)
    }

    @Singleton
    @Provides
    fun provideSubscriptionApi(
        retrofitBuilder: Retrofit.Builder,
        @AuthenticatedClient okHttpClient: OkHttpClient
    ): SubscriptionApi {
        return retrofitBuilder.client(okHttpClient).build().create(SubscriptionApi::class.java)
    }

    @Singleton
    @Provides
    fun provideLikeApi(
        retrofitBuilder: Retrofit.Builder,
        @AuthenticatedClient okHttpClient: OkHttpClient
    ): LikeApi {
        return retrofitBuilder.client(okHttpClient).build().create(LikeApi::class.java)
    }

    @[Provides Singleton]
    fun provideRefreshTokenApi(
        retrofitBuilder: Retrofit.Builder,
        @TokenRefreshClient okHttpClient: OkHttpClient): RefreshTokenApi {
        return retrofitBuilder.client(okHttpClient).build().create(RefreshTokenApi::class.java)
    }
}

/*
    @Singleton
    @Provides
    fun provideOkHttpClient(accessTokenInterceptor: AccessTokenInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(accessTokenInterceptor)
            .build()
    }
*/

/*
    @Singleton
    @Provides
    fun provideTweetApi(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): TweetApi {
        return retrofitBuilder.client(okHttpClient).build().create(TweetApi::class.java)
    }
*/
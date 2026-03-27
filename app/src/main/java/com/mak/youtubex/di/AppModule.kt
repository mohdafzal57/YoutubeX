package com.mak.youtubex.di

import android.app.ActivityManager
import android.content.Context
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.key.Keyer
import coil.memory.MemoryCache
import com.mak.youtubex.BuildConfig
import com.mak.youtubex.core.di.AuthenticatedClient
import com.mak.youtubex.core.di.PublicClient
import com.mak.youtubex.core.di.TokenRefreshClient
import com.mak.youtubex.data.remote.api.LikeApi
import com.mak.youtubex.data.remote.api.RefreshTokenApi
import com.mak.youtubex.data.remote.api.SubscriptionApi
import com.mak.youtubex.data.remote.api.TweetApi
import com.mak.youtubex.data.remote.api.UserApi
import com.mak.youtubex.data.remote.api.VideoApi
import com.mak.youtubex.core.data.network.auth.AuthAuthenticator
import com.mak.youtubex.core.data.network.interceptor.AccessTokenInterceptor
import com.mak.youtubex.core.data.network.interceptor.RefreshTokenInterceptor
import com.mak.youtubex.data.remote.api.PostApi
import com.mak.youtubex.domain.model.LocalVideo
import com.mak.youtubex.utils.MediaStoreThumbnailFetcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlin.jvm.java

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        factory: MediaStoreThumbnailFetcher.Factory
    ): ImageLoader {
        // Dynamic memory budget: safer on low-RAM devices
        val activityManager =
            context.getSystemService(ActivityManager::class.java)
        val appMemoryBytes = activityManager.memoryClass * 1024L * 1024L
        val coilMemoryPercent = if (appMemoryBytes < 128 * 1024 * 1024) {
            0.12 // ≤ 2 GB RAM — be conservative
        } else {
            0.20 // > 2 GB RAM — standard
        }

        return ImageLoader.Builder(context)
            .components {
                add(factory)                    // handles LocalVideo — fast path
                add(VideoFrameDecoder.Factory()) // fallback for Uri/URL video sources
                add(Keyer<LocalVideo> { video, _ -> "video_thumb_${video.id}" })
            }
            .crossfade(false) // disable for scroll lists — instant appearance
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(coilMemoryPercent)
                    .strongReferencesEnabled(true) // survives fast back-scroll
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("video_thumb_cache"))
                    .maxSizeBytes(150L * 1024 * 1024) // 150 MB fixed floor
                    .build()
            }
            .build()
    }

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
        authAuthenticator: AuthAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
        ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(authAuthenticator) //  the AuthAuthenticator automatically takes over to refresh the token and then resends the failed request.
            .addInterceptor(accessTokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /***
        Refresh calls must NOT trigger token refresh
        Otherwise → infinite 401 loop
    ***/
    @[Singleton Provides TokenRefreshClient]
    fun provideRefreshOkHttpClient(
        refreshTokenInterceptor: RefreshTokenInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
        ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(refreshTokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @[Singleton Provides PublicClient]
    fun provideUnAuthenticatedOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
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

    @Singleton
    @Provides
    fun providePostApi(
        retrofitBuilder: Retrofit.Builder,
        @AuthenticatedClient okHttpClient: OkHttpClient
    ): PostApi {
        return retrofitBuilder.client(okHttpClient).build().create(PostApi::class.java)
    }

    @[Provides Singleton]
    fun provideRefreshTokenApi(
        retrofitBuilder: Retrofit.Builder,
        @TokenRefreshClient okHttpClient: OkHttpClient): RefreshTokenApi {
        return retrofitBuilder.client(okHttpClient).build().create(RefreshTokenApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
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
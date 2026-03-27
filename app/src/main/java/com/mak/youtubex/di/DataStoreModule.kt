package com.mak.youtubex.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.mak.youtubex.core.datastore.Encryptor
import com.mak.youtubex.core.datastore.JwtTokenManager
import com.mak.youtubex.core.datastore.TokenManager
import com.mak.youtubex.data.local.PostDao
import com.mak.youtubex.data.local.RemoteKeysDao
import com.mak.youtubex.data.local.YTDatabase
import com.mak.youtubex.data.paging.PostRemoteMediator
import com.mak.youtubex.data.remote.api.PostApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

const val AUTH_PREFERENCES = "auth.preferences_pb"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideAuthDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler {
                emptyPreferences()
            },
            produceFile = {
                appContext.preferencesDataStoreFile(AUTH_PREFERENCES)
            }
        )
    }

    @[Provides Singleton]
    fun provideTokenManager(
        dataStore: DataStore<Preferences>,
        encryptor: Encryptor,
    ): JwtTokenManager {
        return TokenManager(dataStore, encryptor)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): YTDatabase {
        return Room.databaseBuilder(
            context,
            YTDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun providePostDao(db: YTDatabase): PostDao = db.postDao()

    @Provides
    fun provideRemoteKeysDao(db: YTDatabase): RemoteKeysDao = db.remoteKeysDao()
}

@Module
@InstallIn(SingletonComponent::class)
object MediatorModule {

    @Provides
    fun providePostRemoteMediator(
        db: YTDatabase,
        postApi: PostApi
    ): PostRemoteMediator {
        return PostRemoteMediator(db, postApi)
    }
}
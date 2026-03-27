package com.mak.youtubex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [PostEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class YTDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

    abstract fun remoteKeysDao(): RemoteKeysDao
}
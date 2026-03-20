package com.mak.notex.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.mak.notex.domain.model.LocalVideo
import com.mak.notex.domain.model.VideoFolder
import com.mak.notex.domain.repository.LocalMediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocalMediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalMediaRepository {

    override fun getLocalVideos(folderId: Long?): Flow<List<LocalVideo>> = flow {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        )
        val selection = folderId?.let { "${MediaStore.Video.Media.BUCKET_ID} = ?" }
        val selectionArgs = folderId?.let { arrayOf(it.toString()) }

        val videoList = mutableListOf<LocalVideo>()
        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val bucketIdCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                videoList.add(
                    LocalVideo(
                        id = id,
                        uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id),
                        name = cursor.getString(nameCol),
                        duration = cursor.getLong(durationCol),
                        size = cursor.getLong(sizeCol),
                        dateAdded = cursor.getLong(dateCol),
                        folderId = cursor.getLong(bucketIdCol),
                        folderName = cursor.getString(bucketNameCol)
                    )
                )
            }
        }
        emit(videoList)
    }.flowOn(Dispatchers.IO)

    override fun getVideoFolders(): Flow<List<VideoFolder>> = flow {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        )

        val folders = mutableMapOf<Long, VideoFolder>()

        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null, null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val bucketIdCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)


            while (cursor.moveToNext()) {
                val bucketId = cursor.getLong(bucketIdCol)
                val existing = folders[bucketId]

                if (existing == null) {
                    val id = cursor.getLong(idColumn)
                    folders[bucketId] = VideoFolder(
                        id = bucketId,
                        name = cursor.getString(bucketNameCol) ?: "Unknown",
                        thumbnailUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                        ),
                        videoCount = 1
                    )
                } else {
                    folders[bucketId] = existing.copy(
                        videoCount = existing.videoCount + 1
                    )
                }
            }
        }
        emit(folders.values.toList())
    }.flowOn(Dispatchers.IO)
}

/*override fun getLocalVideos(): Flow<List<LocalVideo>> = flow {
       val videoList = mutableListOf<LocalVideo>()

       val projection = arrayOf(
           MediaStore.Video.Media._ID,
           MediaStore.Video.Media.DISPLAY_NAME,
           MediaStore.Video.Media.DURATION,
           MediaStore.Video.Media.SIZE,
           MediaStore.Video.Media.DATE_ADDED
       )

       val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

       context.contentResolver.query(
           MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
           projection,
           null,
           null,
           sortOrder
       )?.use { cursor ->
           val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
           val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
           val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
           val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
           val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

           while (cursor.moveToNext()) {
               val id = cursor.getLong(idColumn)
               val name = cursor.getString(nameColumn)
               val duration = cursor.getLong(durationColumn)
               val size = cursor.getLong(sizeColumn)
               val dateAdded = cursor.getLong(dateColumn)
               val contentUri = ContentUris.withAppendedId(
                   MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                   id
               )

               videoList.add(
                   LocalVideo(
                       id = id,
                       uri = contentUri,
                       name = name,
                       duration = duration,
                       size = size,
                       dateAdded = dateAdded
                   )
               )
           }
       }
       emit(videoList)
   }.flowOn(Dispatchers.IO)*/
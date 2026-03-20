package com.mak.notex.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.LruCache
import android.util.Size
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.Options
import coil.size.Dimension
import com.mak.notex.domain.model.LocalVideo
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.coroutineContext
import androidx.core.graphics.drawable.toDrawable
import kotlinx.coroutines.currentCoroutineContext

@Singleton
class VideoThumbnailRepository2 @Inject constructor(
    @ApplicationContext private val context: Context
) : Closeable {

    private val resolver = context.contentResolver

    // Repository-level scope: survives individual caller cancellation
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    override fun close() {
        job.cancel()
    }

    // Cache key MUST include dimensions — different sizes are different entries
    private data class ThumbnailKey(
        val videoId: Long,
        val width: Int,
        val height: Int
    )

    // 4 MB byte-based LRU (count-based sizing is unsafe for bitmaps)
    private val memoryCache =
        object : LruCache<ThumbnailKey, Bitmap>(4 * 1024 * 1024) {
            override fun sizeOf(key: ThumbnailKey, value: Bitmap): Int =
                value.byteCount
        }

    // computeIfAbsent is atomic — getOrPut is NOT
    private val inFlight =
        ConcurrentHashMap<ThumbnailKey, Deferred<Bitmap?>>()

    suspend fun getThumbnail(
        videoId: Long,
        width: Int,
        height: Int
    ): Bitmap? {
        val key = ThumbnailKey(videoId, width, height)

        // Fast path: already in memory
        memoryCache.get(key)?.let { return it }

        // Deduplicate concurrent requests for the same key
        val deferred = inFlight.computeIfAbsent(key) {
            scope.async {
                try {
                    loadFromMediaStore(videoId, width, height)
                        ?.also { memoryCache.put(key, it) }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    null
                }
            }.also { deferred ->
                deferred.invokeOnCompletion { inFlight.remove(key) }
            }
        }

        return deferred.await()
    }

    private fun loadFromMediaStore(
        videoId: Long,
        width: Int,
        height: Int
    ): Bitmap? {
        val uri = ContentUris.withAppendedId(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            videoId
        )
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.loadThumbnail(
                uri,
                android.util.Size(width, height),
                null
            )
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Video.Thumbnails.getThumbnail(
                resolver,
                videoId,
                MediaStore.Video.Thumbnails.MINI_KIND,
                null
            )
        }
    }
}


// ─────────────────────────────────────────────────────────────
// 2. MediaStoreThumbnailFetcher.kt
// ─────────────────────────────────────────────────────────────
//
// Responsibilities:
//   • Bridges Coil's Fetcher API to VideoThumbnailRepository
//   • Correctly handles Dimension.Pixels / Undefined / Original
//   • dataSource = DISK: tells Coil to write to its disk cache
//   • isSampled = true: prevents Coil serving this for larger sizes
// ─────────────────────────────────────────────────────────────

class MediaStoreThumbnailFetcher2(
    private val data: LocalVideo,
    private val options: Options,
    private val repository: VideoThumbnailRepository2
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        currentCoroutineContext().ensureActive()

        // pxOrElse handles Undefined; `else` branch handles Original
        // (Original has no pixel value and would throw on pxOrElse)
        val width = when (val d = options.size.width) {
            is Dimension.Pixels -> d.px
            else -> 200
        }
        val height = when (val d = options.size.height) {
            is Dimension.Pixels -> d.px
            else -> 200
        }

        val bitmap = repository.getThumbnail(data.id, width, height)
            ?: throw IOException("Thumbnail load failed id=${data.id}")

        return DrawableResult(
            drawable = bitmap.toDrawable(options.context.resources),
            isSampled = true,
            dataSource = DataSource.DISK // correct: system thumbnail cache is on disk
        )
    }

    class Factory @Inject constructor(
        private val repository: VideoThumbnailRepository
    ) : Fetcher.Factory<LocalVideo> {
        override fun create(
            data: LocalVideo,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher = MediaStoreThumbnailFetcher(data, options, repository)
    }
}

@Singleton
class VideoThumbnailRepository @Inject constructor(
    private val resolver: ContentResolver
) {
    // No caching, no scopes! Just pure fetching.
    suspend fun loadFromMediaStore(videoId: Long, width: Int, height: Int): Bitmap? {
        return withContext(Dispatchers.IO) {
            val uri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoId
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resolver.loadThumbnail(uri, Size(width, height), null)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Video.Thumbnails.getThumbnail(
                    resolver, videoId, MediaStore.Video.Thumbnails.MINI_KIND, null
                )
            }
        }
    }
}

class MediaStoreThumbnailFetcher(
    private val data: LocalVideo,
    private val options: Options,
    private val repository: VideoThumbnailRepository
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        val width = if (options.size.width is Dimension.Pixels) (options.size.width as Dimension.Pixels).px else 200
        val height = if (options.size.height is Dimension.Pixels) (options.size.height as Dimension.Pixels).px else 200

        val bitmap = repository.loadFromMediaStore(data.id, width, height)
            ?: throw IOException("Thumbnail load failed id=${data.id}")

        return DrawableResult(
            drawable = bitmap.toDrawable(options.context.resources),
            isSampled = true,
            dataSource = DataSource.DISK
        )
    }
    class Factory @Inject constructor(
        private val repository: VideoThumbnailRepository
    ) : Fetcher.Factory<LocalVideo> {
        override fun create(
            data: LocalVideo,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher = MediaStoreThumbnailFetcher(data, options, repository)
    }
}

interface MediaPrefetcher {
    fun prefetchVideoThumbnails(videos: List<LocalVideo>)
}

class CoilMediaPrefetcher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader
) : MediaPrefetcher {

    private val prefetchScope = CoroutineScope(Dispatchers.Default)

    override fun prefetchVideoThumbnails(videos: List<LocalVideo>) {
        prefetchScope.launch {
            videos.forEach { video ->
                // In CoilMediaPrefetcher
                val cacheKey = "video_thumb_${video.id}" // Create a highly predictable key

                val request = ImageRequest.Builder(context)
                    .data(video)
                    .size(256) // DROP this from 512 to 256 to save massive amounts of RAM
                    .memoryCacheKey(cacheKey) // Explicitly set the key
                    .diskCacheKey(cacheKey)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build()

                imageLoader.enqueue(request)
            }
        }
    }
}
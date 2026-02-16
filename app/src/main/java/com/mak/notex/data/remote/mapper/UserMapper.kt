package com.mak.notex.data.remote.mapper

import com.mak.notex.data.remote.dto.user.RecentWatchedDto
import com.mak.notex.data.remote.dto.user.UserChannelDto
import com.mak.notex.data.remote.dto.user.UserDto
import com.mak.notex.data.remote.dto.user.WatchHistoryDto
import com.mak.notex.domain.model.RecentWatched
import com.mak.notex.domain.model.User
import com.mak.notex.domain.model.UserChannel
import com.mak.notex.domain.model.WatchHistoryItem
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun UserDto.toDomain(): User {
    return User(
        id = id,
        avatar = avatar,
        coverImage = coverImage,
        createdAt = createdAt,
        email = email,
        fullName = fullName,
        updatedAt = updatedAt,
        username = username,
        recentWatched = recentWatched.map { it.toDomain() },
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        avatar = avatar,
        coverImage = coverImage,
        createdAt = createdAt,
        email = email,
        fullName = fullName,
        updatedAt = updatedAt,
        username = username,
        recentWatched = recentWatched.map { it.toDto() },
    )
}

fun RecentWatchedDto.toDomain(): RecentWatched {
    return RecentWatched(
        id = id,
        videoId = videoId,
        watchedAt = watchedAt
    )
}

fun RecentWatched.toDto(): RecentWatchedDto {
    return RecentWatchedDto(
        id = id,
        videoId = videoId,
        watchedAt = watchedAt
    )
}

fun UserChannelDto.toDomain() = UserChannel(
    id = id,
    fullName = fullName,
    username = username,
    email = email,
    avatar = avatar,
    coverImage = coverImage,
    subscribersCount = subscribersCount,
    subscribeToCount = subscribeToCount,
    videosCount = videosCount,
    isUserSubscribed = isUserSubscribed
)

fun UserChannel.toDto() = UserChannelDto(
    id = id,
    fullName = fullName,
    username = username,
    email = email,
    avatar = avatar,
    coverImage = coverImage ?: "",
    subscribersCount = subscribersCount,
    subscribeToCount = subscribeToCount,
    videosCount = videosCount,
    isUserSubscribed = isUserSubscribed
)

fun WatchHistoryDto.toDomain(): WatchHistoryItem {
    val safeDuration = if (duration > 0) duration else 1.0

    return WatchHistoryItem(
        id = id,
        videoId = videoId,
        title = title,
        thumbnail = thumbnail,
        duration = duration,
        watchDuration = watchDuration,
        watchedAt = watchedAt,
        progress = (watchDuration / safeDuration).toFloat()
            .coerceIn(0f, 1f)
    )
}
fun WatchHistoryItem.toDto(): WatchHistoryDto {
    return WatchHistoryDto(
        id = id,
        videoId = videoId,
        title = title,
        thumbnail = thumbnail,
        duration = duration,
        watchDuration = watchDuration,
        watchedAt = watchedAt
    )
}

fun String.toTextRequestBody(): RequestBody =
    this.toRequestBody("text/plain".toMediaType())

fun File.toMultipartImage(fieldName: String): MultipartBody.Part {val requestFile = this.asRequestBody("image/*".toMediaType())
    return MultipartBody.Part.createFormData(fieldName, this.name, requestFile)
}
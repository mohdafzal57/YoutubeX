package com.mak.notex.data.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mak.notex.core.datastore.TokenManager
import com.mak.notex.data.paging.WatchHistoryPagingSource
import com.mak.notex.data.remote.api.UserApi
import com.mak.notex.data.remote.mapper.ProgressRequestBody
import com.mak.notex.data.remote.mapper.toCompressedMultipart
import com.mak.notex.data.remote.mapper.toDomain
import com.mak.notex.data.remote.mapper.toTextRequestBody
import com.mak.notex.data.remote.mapper.toDto
import com.mak.notex.domain.model.ChangePasswordRequest
import com.mak.notex.domain.model.SignInRequest
import com.mak.notex.domain.model.SignInResponse
import com.mak.notex.domain.model.SignUpRequest
import com.mak.notex.domain.model.UpdateAccountDetailRequest
import com.mak.notex.domain.model.User
import com.mak.notex.domain.model.UserChannel
import com.mak.notex.domain.model.WatchHistoryItem
import com.mak.notex.domain.repository.UserRepository
import com.mak.notex.core.data.util.NetworkError
import com.mak.notex.core.data.util.Result
import com.mak.notex.core.data.util.map
import com.mak.notex.core.data.util.onSuccess
import com.mak.notex.core.data.util.safeCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val tokenManager: TokenManager,
    private val contentResolver: ContentResolver
) : UserRepository {

//    override
//    suspend fun signUp(
//        request: SignUpRequest
//    ): Result<User, NetworkError> {
//        val multipart = request.toMultipart(contentResolver)
//
//        return safeCall {
//            userApi.signUp(
//                avatar = multipart.avatar,
//                coverImage = multipart.coverImage,
//                fullName = multipart.fullName,
//                username = multipart.username,
//                email = multipart.email,
//                password = multipart.password
//            )
//        }.map { it.toDomain() }
//    }

    override suspend fun signUp(
        request: SignUpRequest,
        onProgress: (Int) -> Unit
    ): Result<User, NetworkError> {

        var totalBytesUploaded = 0L

        // ---------- Compress Avatar ----------
        val avatarBitmap = contentResolver.openInputStream(request.avatarUri).use {
            BitmapFactory.decodeStream(it)
        }

        val avatarOutput = ByteArrayOutputStream()
        avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 80, avatarOutput)
        val avatarBytes = avatarOutput.toByteArray()

        // ---------- Compress Cover (optional) ----------
        val coverBytes = request.coverUri?.let { uri ->
            val bitmap = contentResolver.openInputStream(uri).use {
                BitmapFactory.decodeStream(it)
            }
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
            output.toByteArray()
        }

        val totalSize =
            avatarBytes.size + (coverBytes?.size ?: 0)

        fun updateProgress(bytes: Long) {
            totalBytesUploaded += bytes
            val progress =
                ((totalBytesUploaded * 100) / totalSize).toInt()
            onProgress(progress.coerceIn(0, 100))
        }

        // ---------- Create Progress RequestBodies ----------
        val avatarRequestBody = ProgressRequestBody(
            avatarBytes.toRequestBody("image/jpeg".toMediaType())
        ) { bytes -> updateProgress(bytes) }

        val coverRequestBody = coverBytes?.let {
            ProgressRequestBody(
                it.toRequestBody("image/jpeg".toMediaType())
            ) { bytes -> updateProgress(bytes) }
        }

        val avatarPart = MultipartBody.Part.createFormData(
            "avatar",
            "avatar.jpg",
            avatarRequestBody
        )

        val coverPart = coverRequestBody?.let {
            MultipartBody.Part.createFormData(
                "coverImage",
                "coverImage.jpg",
                it
            )
        }

        // ---------- API Call ----------
        return safeCall {
            userApi.signUp(
                avatar = avatarPart,
                coverImage = coverPart,
                fullName = request.fullName.toTextRequestBody(),
                username = request.username.toTextRequestBody(),
                email = request.email.toTextRequestBody(),
                password = request.password.toTextRequestBody()
            )
        }.map { it.toDomain() }
    }


    override
    suspend fun signIn(request: SignInRequest): Result<SignInResponse, NetworkError> {
        return safeCall {
            userApi.signIn(request.toDto())
        }.onSuccess { data ->
            tokenManager.saveAccessJwt(data.accessToken)
            tokenManager.saveRefreshJwt(data.refreshToken)
            tokenManager.saveUserDetails(
                id = data.user.id,
                name = data.user.username,
                email = data.user.email
            )
        }.map { it.toDomain() }
    }

    override
    suspend fun signOut(): Result<Unit, NetworkError> {
        return safeCall {
            userApi.signOut()
        }.onSuccess { tokenManager.clearAllTokens() }
    }

    override
    suspend fun getCurrentUser(): Result<User, NetworkError> {
        return safeCall { userApi.getCurrentUser() }.map { it.toDomain() }
    }

    override
    suspend fun getUserChannelProfile(username: String): Result<UserChannel, NetworkError> {
        return safeCall { userApi.getUserChannelProfile(username) }.map { it.toDomain() }
    }

    override
    fun getWatchHistory(): Flow<PagingData<WatchHistoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                WatchHistoryPagingSource(userApi)
            }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override
    suspend fun updateAvatar(avatarUri: Uri): Result<String, NetworkError> {
        val avatarPart = avatarUri.toCompressedMultipart(contentResolver, "avatar")
        return safeCall { userApi.updateAvatar(avatar = avatarPart) }
    }

    override suspend fun updateCoverImage(coverUri: Uri): Result<String, NetworkError> {
        val coverPart = coverUri.toCompressedMultipart(contentResolver, "coverImage")
        return safeCall { userApi.updateCoverImage(coverImage = coverPart) }
    }

    override
    suspend fun updateAccountDetails(
        request: UpdateAccountDetailRequest
    ): Result<User, NetworkError> {
        return safeCall {
            userApi.updateAccountDetails(request.toDto())
        }.map { it.toDomain() }
    }

    override
    suspend fun changePassword(
        request: ChangePasswordRequest
    ): Result<Unit, NetworkError> {
        return safeCall {
            userApi.changePassword(request.toDto())
        }
    }
}

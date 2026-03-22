package com.mak.youtubex.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.mak.youtubex.domain.model.ChangePasswordRequest
import com.mak.youtubex.domain.model.SignInRequest
import com.mak.youtubex.domain.model.SignInResponse
import com.mak.youtubex.domain.model.SignUpRequest
import com.mak.youtubex.domain.model.UpdateAccountDetailRequest
import com.mak.youtubex.domain.model.User
import com.mak.youtubex.domain.model.UserChannel
import com.mak.youtubex.domain.model.WatchHistoryItem
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun signUp(request: SignUpRequest, onProgress: (Int) -> Unit): Result<User, NetworkError>

    suspend fun signIn(request: SignInRequest): Result<SignInResponse, NetworkError>

    suspend fun signOut(): Result<Unit, NetworkError>

    suspend fun getCurrentUser(): Result<User, NetworkError>

    suspend fun getUserChannelProfile(username: String): Result<UserChannel, NetworkError>

    fun getWatchHistory(): Flow<PagingData<WatchHistoryItem>>

    suspend fun updateAvatar(avatarUri: Uri): Result<String, NetworkError>

    suspend fun updateCoverImage(coverUri: Uri): Result<String, NetworkError>

    suspend fun updateAccountDetails(
        request: UpdateAccountDetailRequest
    ): Result<User, NetworkError>

    suspend fun changePassword(
        request: ChangePasswordRequest
    ): Result<Unit, NetworkError>
}

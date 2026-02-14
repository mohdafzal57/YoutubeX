package com.mak.notex.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.mak.notex.data.remote.dto.SignInResponseDto
import com.mak.notex.data.remote.dto.user.UserDto
import com.mak.notex.domain.model.ChangePasswordRequest
import com.mak.notex.domain.model.SignInRequest
import com.mak.notex.domain.model.SignInResponse
import com.mak.notex.domain.model.SignUpRequest
import com.mak.notex.domain.model.UpdateAccountDetailRequest
import com.mak.notex.domain.model.User
import com.mak.notex.domain.model.UserChannel
import com.mak.notex.domain.model.WatchHistoryItem
import com.mak.notex.utils.NetworkError
import com.mak.notex.utils.Result
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

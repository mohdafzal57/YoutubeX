package com.mak.notex.data.remote.api

import com.mak.notex.data.remote.dto.ApiResponse
import com.mak.notex.data.remote.dto.PaginatedResponseDto
import com.mak.notex.data.remote.dto.SignInRequestDto
import com.mak.notex.data.remote.dto.SignInResponseDto
import com.mak.notex.data.remote.dto.user.ChangePasswordRequestDto
import com.mak.notex.data.remote.dto.user.UpdateAccountDetailRequestDto
import com.mak.notex.data.remote.dto.user.UserChannelDto
import com.mak.notex.data.remote.dto.user.UserDto
import com.mak.notex.data.remote.dto.user.WatchHistoryDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @Multipart
    @POST("users/register")
    suspend fun signUp(
        @Part avatar: MultipartBody.Part,
        @Part coverImage: MultipartBody.Part?,
        @Part("fullName") fullName: RequestBody,
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody
    ): Response<ApiResponse<UserDto>>

    @POST("users/login")
    suspend fun signIn(
        @Body request: SignInRequestDto
    ): Response<ApiResponse<SignInResponseDto>>

    @POST("users/logout")
    suspend fun signOut(): Response<ApiResponse<Unit>>

    @GET("users/current-user")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDto>>

    @Multipart
    @PATCH("users/avatar")
    suspend fun updateAvatar(
        @Part avatar: MultipartBody.Part
    ): Response<ApiResponse<String>>

    @Multipart
    @PATCH("users/cover-image")
    suspend fun updateCoverImage(
        @Part coverImage: MultipartBody.Part
    ): Response<ApiResponse<String>>

    @PATCH("users/update-account")
    suspend fun updateAccountDetails(
        @Body request: UpdateAccountDetailRequestDto
    ): Response<ApiResponse<UserDto>>

    @POST("users/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto
    ): Response<ApiResponse<Unit>>

    @GET("users/c/{username}")
    suspend fun getUserChannelProfile(
        @Path("username") username: String
    ): Response<ApiResponse<UserChannelDto>>

    @GET("users/history")
    suspend fun getWatchHistory(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ApiResponse<PaginatedResponseDto<WatchHistoryDto>>>
}

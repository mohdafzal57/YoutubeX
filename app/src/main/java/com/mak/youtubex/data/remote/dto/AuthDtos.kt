package com.mak.youtubex.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.mak.youtubex.data.remote.dto.user.UserDto
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class SignInRequestDto(
    @SerializedName("username")
    val username: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("password")
    val password: String
)

data class SignInResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("user")
    val user: UserDto
)

data class SignUpMultipart(
    val avatar: MultipartBody.Part,
    val coverImage: MultipartBody.Part?,
    val fullName: RequestBody,
    val username: RequestBody,
    val email: RequestBody,
    val password: RequestBody
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class RefreshTokenDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)

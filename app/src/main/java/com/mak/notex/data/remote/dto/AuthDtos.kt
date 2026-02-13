package com.mak.notex.data.remote.dto

import com.mak.notex.data.remote.dto.user.UserDto
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class SignInRequestDto(
    val username: String?,
    val email: String?,
    val password: String
)

data class SignInResponseDto(
    val accessToken: String,
    val refreshToken: String,
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


// Doesn't need of this, since data: {} include UserDto directly!
//data class SignUpResponseDto(
//    val user: UserDto
//)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenDto(
    val accessToken: String,
    val refreshToken: String
)

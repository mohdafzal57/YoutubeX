package com.mak.youtubex.domain.model

import android.net.Uri

data class SignUpRequest(
    val fullName: String,
    val email: String,
    val username: String,
    val password: String,
    val avatarUri: Uri,
    val coverUri: Uri? = null
)

data class SignInRequest(
    val identifier: String,
    val password: String
)

data class SignInResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
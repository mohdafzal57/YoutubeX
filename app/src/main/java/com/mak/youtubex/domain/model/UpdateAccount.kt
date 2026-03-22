package com.mak.youtubex.domain.model

data class UpdateAccountDetailRequest(
    val fullName: String,
    val email: String,
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)
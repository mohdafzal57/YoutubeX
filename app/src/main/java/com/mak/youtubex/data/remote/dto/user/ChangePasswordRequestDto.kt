package com.mak.youtubex.data.remote.dto.user

data class ChangePasswordRequestDto(
    val oldPassword: String,
    val newPassword: String,
)
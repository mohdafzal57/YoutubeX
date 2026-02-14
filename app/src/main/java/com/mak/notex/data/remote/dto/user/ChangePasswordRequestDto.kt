package com.mak.notex.data.remote.dto.user

data class ChangePasswordRequestDto(
    val oldPassword: String,
    val newPassword: String,
)
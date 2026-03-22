package com.mak.youtubex.data.remote.mapper

import com.mak.youtubex.data.remote.dto.user.ChangePasswordRequestDto
import com.mak.youtubex.data.remote.dto.user.UpdateAccountDetailRequestDto
import com.mak.youtubex.domain.model.ChangePasswordRequest
import com.mak.youtubex.domain.model.UpdateAccountDetailRequest

fun UpdateAccountDetailRequest.toDto(): UpdateAccountDetailRequestDto {
    return UpdateAccountDetailRequestDto(
        fullName = fullName,
        email = email
    )
}

fun ChangePasswordRequest.toDto(): ChangePasswordRequestDto {
    return ChangePasswordRequestDto(
        oldPassword = oldPassword,
        newPassword = newPassword
    )
}

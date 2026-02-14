package com.mak.notex.data.remote.mapper

import com.mak.notex.data.remote.dto.user.ChangePasswordRequestDto
import com.mak.notex.data.remote.dto.user.UpdateAccountDetailRequestDto
import com.mak.notex.domain.model.ChangePasswordRequest
import com.mak.notex.domain.model.UpdateAccountDetailRequest

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

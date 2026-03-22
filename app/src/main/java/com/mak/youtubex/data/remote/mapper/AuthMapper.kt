package com.mak.youtubex.data.remote.mapper

import android.content.ContentResolver
import android.util.Patterns
import com.mak.youtubex.data.remote.dto.SignInRequestDto
import com.mak.youtubex.data.remote.dto.SignInResponseDto
import com.mak.youtubex.data.remote.dto.SignUpMultipart
import com.mak.youtubex.domain.model.SignInRequest
import com.mak.youtubex.domain.model.SignInResponse
import com.mak.youtubex.domain.model.SignUpRequest

fun SignInResponseDto.toDomain(): SignInResponse {
    return SignInResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toDomain()
    )
}

fun SignInRequest.toDto(): SignInRequestDto {
    val isEmail = Patterns.EMAIL_ADDRESS.matcher(identifier).matches()
    return SignInRequestDto(
        username = if (isEmail) null else identifier,
        email = if (isEmail) identifier else null,
        password = password
    )
}

//fun SignUpRequest.toMultipart(
//    resolver: ContentResolver
//): SignUpMultipart {
//    return SignUpMultipart(
//        avatar = avatarUri.toCompressedMultipart(resolver, "avatar"),
//        coverImage = coverUri?.toCompressedMultipart(resolver, "coverImage"),
//        fullName = fullName.toMultipartText(),
//        username = username.toMultipartText(),
//        email = email.toMultipartText(),
//        password = password.toMultipartText()
//    )
//}

fun SignUpRequest.toMultipart(
    resolver: ContentResolver,
    onBytesWritten: (Long) -> Unit
): SignUpMultipart {
    return SignUpMultipart(
        avatar = avatarUri.toCompressedMultipart(
            resolver,
            "avatar",
            onBytesWritten = onBytesWritten
        ),
        coverImage = coverUri?.toCompressedMultipart(
            resolver,
            "coverImage",
            onBytesWritten = onBytesWritten
        ),
        fullName = fullName.toTextRequestBody(),
        username = username.toTextRequestBody(),
        email = email.toTextRequestBody(),
        password = password.toTextRequestBody()
    )
}

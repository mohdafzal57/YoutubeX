package com.mak.youtubex.data.remote.api

import com.mak.youtubex.data.remote.dto.ApiResponse
import com.mak.youtubex.data.remote.dto.RefreshTokenDto
import retrofit2.Response
import retrofit2.http.POST

interface RefreshTokenApi {

    @POST("users/refresh-token")
    suspend fun refreshToken(): Response<ApiResponse<RefreshTokenDto>>
}
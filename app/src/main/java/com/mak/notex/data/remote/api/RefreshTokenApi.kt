package com.mak.notex.data.remote.api

import com.mak.notex.data.remote.dto.ApiResponse
import com.mak.notex.data.remote.dto.RefreshTokenDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshTokenApi {

    @POST("users/refresh-token")
    suspend fun refreshToken(): Response<ApiResponse<RefreshTokenDto>>
}
package com.mak.youtubex.core.data.network.interceptor

import com.mak.youtubex.core.datastore.JwtTokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RefreshTokenInterceptor @Inject constructor(
    private val tokenManager: JwtTokenManager
) : Interceptor {
    companion object {
        const val AUTH_HEADER = "Authorization"
        const val BEARER = "Bearer"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenManager.getRefreshJwt().first()
        }
        val request = chain.request().newBuilder()
        if (token != null) {
            request.addHeader(AUTH_HEADER, "$BEARER $token")
        }
        return chain.proceed(request.build())
    }
}
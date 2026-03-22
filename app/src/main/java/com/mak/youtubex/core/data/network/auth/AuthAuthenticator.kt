package com.mak.youtubex.core.data.network.auth

import com.mak.youtubex.data.remote.api.RefreshTokenApi
import com.mak.youtubex.core.datastore.JwtTokenManager
import com.mak.youtubex.core.data.util.onFailure
import com.mak.youtubex.core.data.util.onSuccess
import com.mak.youtubex.core.data.util.safeCall
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val jwtTokenManager: JwtTokenManager,
    private val refreshTokenApi: RefreshTokenApi,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        val retryCount = response.request.header(RETRY_COUNT)?.toIntOrNull() ?: 0
        if (retryCount >= MAX_RETRIES) {
            return null
        }
        val currentToken = runBlocking {
            jwtTokenManager.getAccessJwt().first()
        }
        synchronized(this) {
            val newToken = runBlocking {
                jwtTokenManager.getAccessJwt().first()
            }
            var token: String? = null
            if (currentToken != newToken) {
                token = newToken
            } else {
                val newSessionResponse = runBlocking {
                    safeCall { refreshTokenApi.refreshToken() }
                }
                newSessionResponse.onSuccess { response ->
                    runBlocking {
                        jwtTokenManager.saveAccessJwt(response.accessToken)
                        jwtTokenManager.saveRefreshJwt(response.refreshToken)
                    }
                    token = response.accessToken
                }
                newSessionResponse.onFailure {
                    runBlocking { jwtTokenManager.clearAllTokens() } // log user out
                    token = null
                }
            }
            return if (token != null) {
                response.request.newBuilder()
                    .header(HEADER_AUTHORIZATION, "$TOKEN_TYPE $token")
                    .header(RETRY_COUNT, "${retryCount + 1}")
                    .build()
            } else {
                runBlocking { jwtTokenManager.clearAllTokens() } // log user out
                null
            }
        }
    }
    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val TOKEN_TYPE = "Bearer"
        const val RETRY_COUNT = "Retry-Count"
        const val MAX_RETRIES = 3
    }
}

/***
fun refreshToken(currentToken: String?, newToken: String?): String? {
    val token = if (currentToken != newToken) newToken else {
        val newSessionResponse = runBlocking { refreshTokenApi.refreshToken() }
        if (newSessionResponse.isSuccessful && newSessionResponse.body() != null) {
            newSessionResponse.body()?.data?.let { tokenData ->
                runBlocking {
                    jwtTokenManager.saveAccessJwt(tokenData.accessToken)
                    jwtTokenManager.saveRefreshJwt(tokenData.refreshToken)
                }
                tokenData.accessToken
            }
        } else {
            runBlocking { jwtTokenManager.clearAllTokens() } // log user out
            null
        }
    }
    return token
}
***/

/*
class AuthAuthenticator @Inject constructor(
    private val jwtTokenManager: JwtTokenManager,
    private val refreshTokenApi: RefreshTokenApi,
) : Authenticator {

    // Use a Mutex for coroutine-safe locking instead of 'synchronized'
    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. Fail fast if we've already retried too many times
        if (responseCount(response) >= MAX_RETRIES) {
            return null // Give up
        }

        // 2. Run everything in ONE runBlocking context
        return runBlocking {
            val currentToken = jwtTokenManager.getAccessJwt().first()

            // 3. Use Mutex to handle the "Thundering Herd"
            mutex.withLock {
                // Double-check: Did someone else refresh while we were waiting?
                val freshToken = jwtTokenManager.getAccessJwt().first()

                if (currentToken != freshToken) {
                    // Yes, someone refreshed it! Just use the new token.
                    rewriteRequest(response.request, freshToken)
                } else {
                    // No, we are the chosen one. Perform the refresh.
                    val refreshResponse = safeCall { refreshTokenApi.refreshToken() }

                    refreshResponse.getOrNull()?.let { tokenData ->
                        jwtTokenManager.saveAccessJwt(tokenData.accessToken)
                        jwtTokenManager.saveRefreshJwt(tokenData.refreshToken)
                        rewriteRequest(response.request, tokenData.accessToken)
                    } ?: run {
                        // Refresh failed
                        jwtTokenManager.clearAllTokens()
                        null
                    }
                }
            }
        }
    }

    private fun rewriteRequest(request: Request, newToken: String): Request {
        return request.newBuilder()
            .header(HEADER_AUTHORIZATION, "$TOKEN_TYPE $newToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val TOKEN_TYPE = "Bearer"
        private const val MAX_RETRIES = 3
    }
}
*/
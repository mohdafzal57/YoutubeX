package com.mak.youtubex.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.youtubex.core.datastore.JwtTokenManager
import com.mak.youtubex.presentation.auth.AuthState.Loading
import com.mak.youtubex.presentation.auth.AuthState.Authenticated
import com.mak.youtubex.presentation.auth.AuthState.Unauthenticated
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenManager: JwtTokenManager
) : ViewModel() {

    val authState: StateFlow<AuthState> =
        tokenManager.session
            .map { userSession ->
                if (userSession.isLoggedIn) {
                    Authenticated(userSession.avatar)
                } else {
                    Unauthenticated
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Loading
            )
}

sealed class AuthState {

    object Loading : AuthState()

    data class Authenticated(
        val avatar: String?
    ): AuthState()

    object Unauthenticated: AuthState()

    fun shouldKeepSplashScreen() = this is Loading
}
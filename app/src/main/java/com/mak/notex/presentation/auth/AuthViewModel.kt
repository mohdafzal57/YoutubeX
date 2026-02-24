package com.mak.notex.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.notex.core.datastore.JwtTokenManager
import com.mak.notex.core.datastore.TokenManager
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
        tokenManager.getAccessJwt()
            .map { token ->
                if (token.isNullOrBlank()) {
                    AuthState.Unauthenticated
                } else {
                    AuthState.Authenticated
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AuthState.Loading
            )
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
}
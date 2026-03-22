package com.mak.youtubex.presentation.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.youtubex.R
import com.mak.youtubex.domain.model.SignInRequest
import com.mak.youtubex.domain.repository.UserRepository
import com.mak.youtubex.core.util.UiText
import com.mak.youtubex.utils.asStringRes
import com.mak.youtubex.core.data.util.onFailure
import com.mak.youtubex.core.data.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SignInEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: SignInIntent) {
        when (intent) {
            is SignInIntent.IdentifierChanged -> {
                _state.update {
                    it.copy(identifier = intent.identifier, identifierError = null)
                }
            }

            is SignInIntent.PasswordChanged -> {
                _state.update {
                    it.copy(password = intent.password, passwordError = null)
                }
            }

            is SignInIntent.SignInClicked -> signIn()
            is SignInIntent.ErrorDismissed -> {
                // Clear all errors
                _state.update {
                    it.copy(identifierError = null, passwordError = null)
                }
            }
        }
    }

    private fun signIn() {
        val currentState = _state.value

        // 1. Validation Logic
        val identifierError = validateIdentifier(currentState.identifier)
        val passwordError = validatePassword(currentState.password)

        // If any validation failed, update state and return
        if (identifierError != null || passwordError != null) {
            _state.update {
                it.copy(
                    identifierError = identifierError,
                    passwordError = passwordError
                )
            }
            return
        }

        val signInRequest = SignInRequest(
            identifier = currentState.identifier,
            password = currentState.password
        )

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            userRepository.signIn(signInRequest)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(SignInEffect.NavigateToHome)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(
                        SignInEffect.ShowSnackbarError(
                            UiText.StringResource(error.asStringRes())
                        )
                    )
                }
        }
    }

    // --- Helpers ---

    private fun validateIdentifier(identifier: String): UiText? {
        return if (identifier.isBlank()) {
            UiText.StringResource(R.string.error_identifier_empty)
        } else if(identifier.length < 3) {
            UiText.StringResource(R.string.error_identifier_too_short)
        } else {
            null
        }
    }

    private fun validatePassword(password: String): UiText? {
        return if (password.isBlank()) {
            UiText.StringResource(R.string.error_password_empty)
        } else if (password.length < 4) {
            UiText.StringResource(R.string.error_password_too_short, 4)
        } else {
            null
        }
    }
}
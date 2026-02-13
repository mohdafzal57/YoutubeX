package com.mak.notex.presentation.auth.signin

import com.mak.notex.utils.UiText

data class SignInState(
    val identifier: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    // Change: Error is now UiText, allowing ResId or String
    val identifierError: UiText? = null,
    val passwordError: UiText? = null
) {
    // Computed property (Logic in State, not UI)
    val isSubmitEnabled: Boolean
        get() = identifier.isNotBlank() && password.isNotBlank() && !isLoading

    // Security: Mask password in logs
    override fun toString(): String {
        return "SignInState(identifier='$identifier', password='***', isLoading=$isLoading, error=$identifierError)"
    }
}

sealed interface SignInIntent {
    data class IdentifierChanged(val identifier: String) : SignInIntent
    data class PasswordChanged(val password: String) : SignInIntent
    data object SignInClicked : SignInIntent
    data object ErrorDismissed : SignInIntent
}

sealed interface SignInEffect {
    data object NavigateToHome : SignInEffect

    // Change: Payload is UiText
    data class ShowSnackbarError(val message: UiText) : SignInEffect
}

package com.mak.notex.presentation.auth.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.notex.domain.model.SignUpRequest
import com.mak.notex.domain.repository.UserRepository
import com.mak.notex.presentation.auth.signup.SignUpContract.Effect
import com.mak.notex.presentation.auth.signup.SignUpContract.Event
import com.mak.notex.presentation.auth.signup.SignUpContract.State
import com.mak.notex.core.util.UiText
import com.mak.notex.utils.asStringRes
import com.mak.notex.core.data.util.onFailure
import com.mak.notex.core.data.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Sign Up screen.
 * Implements MVI pattern using StateFlow for state and Channel for side effects.
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    /**
     * Entry point for all UI events.
     */
    fun onEvent(event: Event) {
        when (event) {
            is Event.OnFullNameChanged -> handleFullNameChanged(event.fullName)
            is Event.OnUsernameChanged -> handleUsernameChanged(event.username)
            is Event.OnEmailChanged -> handleEmailChanged(event.email)
            is Event.OnPasswordChanged -> handlePasswordChanged(event.password)
            is Event.OnAvatarSelected -> handleAvatarSelected(event.uri)
            is Event.OnCoverImageSelected -> handleCoverImageSelected(event.uri)
            is Event.OnAvatarPickerClicked -> sendEffect(Effect.OpenAvatarPicker)
            is Event.OnCoverImagePickerClicked -> sendEffect(Effect.OpenCoverImagePicker)
            is Event.OnSignUpClicked -> handleSignUp()
            is Event.OnNavigateBackClicked -> sendEffect(Effect.NavigateBack)
            is Event.OnNavigateToSignInClicked -> sendEffect(Effect.NavigateToSignIn)
            is Event.DismissError -> dismissError()
        }
    }

    private fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun handleFullNameChanged(fullName: String) {
        _state.update { currentState ->
            currentState.copy(
                fullName = fullName,
                validationErrors = currentState.validationErrors.copy(
                    fullName = validateFullName(fullName)
                )
            )
        }
    }

    private fun handleUsernameChanged(username: String) {
        _state.update { currentState ->
            currentState.copy(
                username = username,
                validationErrors = currentState.validationErrors.copy(
                    username = validateUsername(username)
                )
            )
        }
    }

    private fun handleEmailChanged(email: String) {
        _state.update { currentState ->
            currentState.copy(
                email = email,
                validationErrors = currentState.validationErrors.copy(
                    email = validateEmail(email)
                )
            )
        }
    }

    private fun handlePasswordChanged(password: String) {
        _state.update { currentState ->
            currentState.copy(
                password = password,
                validationErrors = currentState.validationErrors.copy(
                    password = validatePassword(password)
                )
            )
        }
    }

    private fun handleAvatarSelected(uri: android.net.Uri) {
        _state.update { currentState ->
            currentState.copy(
                avatarUri = uri,
                validationErrors = currentState.validationErrors.copy(avatar = null)
            )
        }
    }

    private fun handleCoverImageSelected(uri: android.net.Uri?) {
        _state.update { it.copy(coverImageUri = uri) }
    }

    private fun dismissError() {
        _state.update { it.copy(validationErrors = State.ValidationErrors()) }
    }

    private fun handleSignUp() {
        val currentState = _state.value
        val errors = State.ValidationErrors(
            fullName = validateFullName(currentState.fullName),
            username = validateUsername(currentState.username),
            email = validateEmail(currentState.email),
            password = validatePassword(currentState.password),
            avatar = if (currentState.avatarUri == null) {
                UiText.DynamicString("Avatar is required")
            } else null
        )

        if (errors.hasErrors) {
            _state.update { it.copy(validationErrors = errors) }
            return
        }

        performSignUp()
    }

    private fun performSignUp() {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, uploadProgress = 0) }

            val request = SignUpRequest(
                fullName = _state.value.fullName,
                username = _state.value.username,
                email = _state.value.email,
                password = _state.value.password,
                avatarUri = _state.value.avatarUri!!,
                coverUri = _state.value.coverImageUri
            )

            userRepository.signUp(request) { progress ->
                _state.update { it.copy(uploadProgress = progress) }
            }.onSuccess {
                _state.update { it.copy(isLoading = false, uploadProgress = 100) }
                sendEffect(
                    Effect.ShowSuccess(
                        UiText.DynamicString("Account created successfully!")
                    )
                )
                sendEffect(Effect.NavigateToSignIn)
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false) }
                sendEffect(
                    Effect.ShowError(
                        UiText.StringResource(error.asStringRes())
                    )
                )
            }
        }
    }
}


// Validation functions - Industry standard often uses dedicated use cases or utility classes
// for validation, but for simple flows, keeping them here is acceptable.
private fun validateFullName(name: String): UiText? {
    return when {
        name.isBlank() -> UiText.DynamicString("Full name is required")
        name.length < 2 -> UiText.DynamicString("Full name must be at least 2 characters")
        name.length > 50 -> UiText.DynamicString("Full name must be less than 50 characters")
        !name.matches(Regex("^[a-zA-Z\\s]+$")) -> UiText.DynamicString("Full name can only contain letters")
        else -> null
    }
}

private fun validateUsername(username: String): UiText? {
    return when {
        username.isBlank() -> UiText.DynamicString("Username is required")
        username.length < 3 -> UiText.DynamicString("Username must be at least 3 characters")
        username.length > 20 -> UiText.DynamicString("Username must be less than 20 characters")
        !username.matches(Regex("^[a-zA-Z0-9_]+$")) ->
            UiText.DynamicString("Username can only contain letters, numbers, and underscores")

        else -> null
    }
}

private fun validateEmail(email: String): UiText? {
    return when {
        email.isBlank() -> UiText.DynamicString("Email is required")
        !Patterns.EMAIL_ADDRESS.matcher(email)
            .matches() -> UiText.DynamicString("Invalid email address")

        else -> null
    }
}

private fun validatePassword(password: String): UiText? {
    return when {
        password.isBlank() -> UiText.DynamicString("Password is required")
        password.length < 4 -> UiText.DynamicString("Password must be at least 6 characters")
        password.length > 50 -> UiText.DynamicString("Password must be less than 50 characters")
        !password.any { it.isDigit() } -> UiText.DynamicString("Password must contain at least one number")
        !password.any { it.isLetter() } -> UiText.DynamicString("Password must contain at least one letter")
        else -> null
    }
}


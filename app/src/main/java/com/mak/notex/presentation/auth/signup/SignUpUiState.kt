package com.mak.notex.presentation.auth.signup

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.mak.notex.utils.UiText

/**
 * MVI Contract for SignUp feature
 * Defines the state, events, and side effects for the signup flow
 */
object SignUpContract {

    /**
     * Represents the UI state for the signup screen
     * Marked as Immutable to optimize recompositions in Jetpack Compose
     */
    @Immutable
    data class State(
        val fullName: String = "",
        val username: String = "",
        val email: String = "",
        val password: String = "",
        val avatarUri: Uri? = null,
        val coverImageUri: Uri? = null,
        val isLoading: Boolean = false,
        val uploadProgress: Int = 0,
        val validationErrors: ValidationErrors = ValidationErrors()
    ) {
        @Immutable
        data class ValidationErrors(
            val fullName: UiText? = null,
            val username: UiText? = null,
            val email: UiText? = null,
            val password: UiText? = null,
            val avatar: UiText? = null
        ) {
            val hasErrors: Boolean
                get() = fullName != null || username != null ||
                        email != null || password != null || avatar != null
        }

        val isFormValid: Boolean
            get() = fullName.isNotBlank() &&
                    username.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    avatarUri != null &&
                    !validationErrors.hasErrors
    }

    /**
     * User events/intents from the UI
     */
    sealed interface Event {
        data class OnFullNameChanged(val fullName: String) : Event
        data class OnUsernameChanged(val username: String) : Event
        data class OnEmailChanged(val email: String) : Event
        data class OnPasswordChanged(val password: String) : Event
        data class OnAvatarSelected(val uri: Uri) : Event
        data class OnCoverImageSelected(val uri: Uri?) : Event
        data object OnAvatarPickerClicked : Event
        data object OnCoverImagePickerClicked : Event
        data object OnSignUpClicked : Event
        data object OnNavigateBackClicked : Event
        data object OnNavigateToSignInClicked : Event
        data object DismissError : Event
    }

    /**
     * One-time side effects
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data object NavigateToSignIn : Effect
        data object OpenAvatarPicker : Effect
        data object OpenCoverImagePicker : Effect
        data class ShowError(val message: UiText) : Effect
        data class ShowSuccess(val message: UiText) : Effect
    }
}

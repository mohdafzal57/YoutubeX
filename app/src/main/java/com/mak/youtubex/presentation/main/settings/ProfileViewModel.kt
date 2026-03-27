package com.mak.youtubex.presentation.main.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.youtubex.domain.model.ChangePasswordRequest
import com.mak.youtubex.domain.model.UpdateAccountDetailRequest
import com.mak.youtubex.domain.model.User
import com.mak.youtubex.domain.repository.UserRepository
import com.mak.youtubex.utils.formatDate
import com.mak.youtubex.core.data.util.onFailure
import com.mak.youtubex.core.data.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _events = Channel<ProfileEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadUserProfile()
    }

    fun onAction(intent: ProfileAction) {
        when (intent) {
            is ProfileAction.LoadUserProfile -> loadUserProfile()
            is ProfileAction.UpdateAvatar -> updateAvatar(intent.uri)
            is ProfileAction.UpdateCoverImage -> updateCoverImage(intent.uri)
            is ProfileAction.UpdateAccountDetails -> updateAccountDetails(
                intent.fullName,
                intent.email
            )

            is ProfileAction.ChangePassword -> changePassword(
                intent.oldPassword,
                intent.newPassword
            )

            is ProfileAction.Logout -> logout()
            is ProfileAction.ShowEditProfileDialog -> _state.update { it.copy(showEditDialog = true) }
            is ProfileAction.DismissEditProfileDialog -> _state.update { it.copy(showEditDialog = false) }
            is ProfileAction.ShowChangePasswordDialog -> _state.update {
                it.copy(
                    showChangePasswordDialog = true
                )
            }

            is ProfileAction.DismissChangePasswordDialog -> _state.update {
                it.copy(
                    showChangePasswordDialog = false
                )
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            userRepository.getCurrentUser()
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            userProfile = user.toUserProfile(),
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.toString(), isLoading = false) }
                    _events.send(ProfileEvent.ShowError(error.toString()))
                }
        }
    }

    private fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.updateAvatar(uri)
                .onSuccess { avatar ->
                    _state.update {
                        it.copy(
                            userProfile = it.userProfile?.copy(avatar = avatar),
                            isLoading = false
                        )
                    }
                    _events.send(ProfileEvent.ShowMessage("Avatar updated"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.ShowError(error.toString()))
                }
        }
    }

    private fun updateCoverImage(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.updateCoverImage(uri)
                .onSuccess { coverImage ->
                    _state.update {
                        it.copy(
                            userProfile = _state.value.userProfile?.copy(coverImage = coverImage),
                            isLoading = false
                        )
                    }
                    _events.send(ProfileEvent.ShowMessage("Cover image updated"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.ShowError(error.toString()))
                }
        }
    }

    private fun updateAccountDetails(fullName: String, email: String) {
        viewModelScope.launch {
            val request = UpdateAccountDetailRequest(
                fullName = fullName,
                email = email
            )
            _state.update { it.copy(isLoading = true, showEditDialog = false) }
            userRepository.updateAccountDetails(request)
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            userProfile = _state.value.userProfile?.copy(
                                fullName = user.fullName, email = user.email
                            ),
                            isLoading = false
                        )
                    }
                    _events.send(ProfileEvent.ShowMessage("Profile updated"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.ShowError(error.toString()))
                }
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                _events.send(ProfileEvent.ShowError("Password cannot be empty"))
                return@launch
            }
            val request = ChangePasswordRequest(
                oldPassword = oldPassword,
                newPassword = newPassword
            )
            _state.update { it.copy(isLoading = true, showChangePasswordDialog = false) }
            userRepository.changePassword(request)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.ShowMessage("Password changed"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.ShowError(error.toString()))
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.signOut()
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.NavigateToLogin)
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.ShowError(it.toString()))
                }
        }
    }
}

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showEditDialog: Boolean = false,
    val showChangePasswordDialog: Boolean = false
)

sealed interface ProfileAction {
    data object LoadUserProfile : ProfileAction
    data class UpdateAvatar(val uri: Uri) : ProfileAction
    data class UpdateCoverImage(val uri: Uri) : ProfileAction
    data class UpdateAccountDetails(val fullName: String, val email: String) : ProfileAction
    data class ChangePassword(val oldPassword: String, val newPassword: String) : ProfileAction
    data object Logout : ProfileAction
    data object ShowEditProfileDialog : ProfileAction
    data object DismissEditProfileDialog : ProfileAction
    data object ShowChangePasswordDialog : ProfileAction
    data object DismissChangePasswordDialog : ProfileAction
}

sealed interface ProfileEvent {
    data class ShowMessage(val message: String) : ProfileEvent
    data class ShowError(val error: String) : ProfileEvent
    data object NavigateToLogin : ProfileEvent
}

data class UserProfile(
    val avatar: String,
    val coverImage: String?,
    val email: String,
    val fullName: String,
    val username: String,
    val createdAt: String,
)

fun User.toUserProfile() = UserProfile(
    avatar = avatar,
    coverImage = coverImage ?: "",
    email = email,
    fullName = fullName,
    username = username,
    createdAt = formatDate(createdAt)
)
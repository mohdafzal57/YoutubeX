package com.mak.notex.presentation.main.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.notex.domain.model.ChangePasswordRequest
import com.mak.notex.domain.model.UpdateAccountDetailRequest
import com.mak.notex.domain.model.User
import com.mak.notex.domain.repository.UserRepository
import com.mak.notex.utils.formatDate
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

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val _events = Channel<SettingsEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadUserProfile()
    }

    fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.LoadUserProfile -> loadUserProfile()
            is SettingsIntent.UpdateAvatar -> updateAvatar(intent.uri)
            is SettingsIntent.UpdateCoverImage -> updateCoverImage(intent.uri)
            is SettingsIntent.UpdateAccountDetails -> updateAccountDetails(
                intent.fullName,
                intent.email
            )

            is SettingsIntent.ChangePassword -> changePassword(
                intent.oldPassword,
                intent.newPassword
            )

            is SettingsIntent.Logout -> logout()
            is SettingsIntent.ShowEditProfileDialog -> _state.update { it.copy(showEditDialog = true) }
            is SettingsIntent.DismissEditProfileDialog -> _state.update { it.copy(showEditDialog = false) }
            is SettingsIntent.ShowChangePasswordDialog -> _state.update {
                it.copy(
                    showChangePasswordDialog = true
                )
            }

            is SettingsIntent.DismissChangePasswordDialog -> _state.update {
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
                            userProfile = user.toUserProfileState(),
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.toString(), isLoading = false) }
                    _events.send(SettingsEvent.ShowError(error.toString()))
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
                    _events.send(SettingsEvent.ShowMessage("Avatar updated"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SettingsEvent.ShowError(error.toString()))
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
                    _events.send(SettingsEvent.ShowMessage("Cover image updated"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SettingsEvent.ShowError(error.toString()))
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
                    _events.send(SettingsEvent.ShowMessage("Profile updated"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SettingsEvent.ShowError(error.toString()))
                }
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                _events.send(SettingsEvent.ShowError("Password cannot be empty"))
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
                    _events.send(SettingsEvent.ShowMessage("Password changed"))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SettingsEvent.ShowError(error.toString()))
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.signOut()
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SettingsEvent.NavigateToLogin)
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SettingsEvent.ShowError(it.toString()))
                }
        }
    }
}

data class SettingsState(
    val userProfile: UserProfileState? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showEditDialog: Boolean = false,
    val showChangePasswordDialog: Boolean = false
)

sealed interface SettingsIntent {
    data object LoadUserProfile : SettingsIntent
    data class UpdateAvatar(val uri: Uri) : SettingsIntent
    data class UpdateCoverImage(val uri: Uri) : SettingsIntent
    data class UpdateAccountDetails(val fullName: String, val email: String) : SettingsIntent
    data class ChangePassword(val oldPassword: String, val newPassword: String) : SettingsIntent
    data object Logout : SettingsIntent
    data object ShowEditProfileDialog : SettingsIntent
    data object DismissEditProfileDialog : SettingsIntent
    data object ShowChangePasswordDialog : SettingsIntent
    data object DismissChangePasswordDialog : SettingsIntent
}

sealed interface SettingsEvent {
    data class ShowMessage(val message: String) : SettingsEvent
    data class ShowError(val error: String) : SettingsEvent
    data object NavigateToLogin : SettingsEvent
}

data class UserProfileState(
    val avatar: String,
    val coverImage: String?,
    val email: String,
    val fullName: String,
    val username: String,
    val createdAt: String,
)

fun User.toUserProfileState() = UserProfileState(
    avatar = avatar,
    coverImage = coverImage ?: "",
    email = email,
    fullName = fullName,
    username = username,
    createdAt = formatDate(createdAt)
)
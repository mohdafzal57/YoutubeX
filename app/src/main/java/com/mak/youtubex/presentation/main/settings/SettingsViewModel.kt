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
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _events = Channel<SettingsEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadUserProfile()
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.LoadUserSettings -> loadUserProfile()
            is SettingsAction.UpdateAvatar -> updateAvatar(action.uri)
            is SettingsAction.UpdateCoverImage -> updateCoverImage(action.uri)
            is SettingsAction.UpdateAccountDetails -> updateAccountDetails(
                action.fullName,
                action.email
            )

            is SettingsAction.ChangePassword -> changePassword(
                action.oldPassword,
                action.newPassword
            )

            is SettingsAction.Logout -> logout()
            is SettingsAction.ShowEditSettingsDialog -> _state.update { it.copy(showEditDialog = true) }
            is SettingsAction.DismissEditSettingsDialog -> _state.update { it.copy(showEditDialog = false) }
            is SettingsAction.ShowChangePasswordDialog -> _state.update {
                it.copy(
                    showChangePasswordDialog = true
                )
            }

            is SettingsAction.DismissChangePasswordDialog -> _state.update {
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
            _state.update { it.copy(isLoading = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("VIEWMODEL_S Profile cleared")
    }
}

data class SettingsUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showEditDialog: Boolean = false,
    val showChangePasswordDialog: Boolean = false
)

sealed interface SettingsAction {
    data object LoadUserSettings : SettingsAction
    data class UpdateAvatar(val uri: Uri) : SettingsAction
    data class UpdateCoverImage(val uri: Uri) : SettingsAction
    data class UpdateAccountDetails(val fullName: String, val email: String) : SettingsAction
    data class ChangePassword(val oldPassword: String, val newPassword: String) : SettingsAction
    data object Logout : SettingsAction
    data object ShowEditSettingsDialog : SettingsAction
    data object DismissEditSettingsDialog : SettingsAction
    data object ShowChangePasswordDialog : SettingsAction
    data object DismissChangePasswordDialog : SettingsAction
}

sealed interface SettingsEvent {
    data class ShowMessage(val message: String) : SettingsEvent
    data class ShowError(val error: String) : SettingsEvent
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
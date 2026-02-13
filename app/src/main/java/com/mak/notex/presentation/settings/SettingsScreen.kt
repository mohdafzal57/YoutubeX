package com.mak.notex.presentation.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mak.notex.presentation.common.LoadingScreen
import com.mak.notex.presentation.navigation.LocalSnackbarHostState

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }

                is SettingsEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.error,
                        duration = SnackbarDuration.Long,
                        actionLabel = "Dismiss"
                    )
                }

                is SettingsEvent.NavigateToLogin -> onLogoutSuccess()
            }
        }
    }

    val avatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.handleIntent(SettingsIntent.UpdateAvatar(it)) }
    }

    val coverImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.handleIntent(SettingsIntent.UpdateCoverImage(it)) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading && state.userProfile == null -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.error != null && state.userProfile == null -> {
                ErrorContent(
                    error = state.error!!,
                    onRetry = { viewModel.handleIntent(SettingsIntent.LoadUserProfile) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }


            state.userProfile != null -> {
                SettingsContent(
                    userProfile = state.userProfile!!,
                    onAvatarClick = { avatarLauncher.launch("image/*") },
                    onCoverImageClick = { coverImageLauncher.launch("image/*") },
                    onEditProfile = { viewModel.handleIntent(SettingsIntent.ShowEditProfileDialog) },
                    onChangePassword = { viewModel.handleIntent(SettingsIntent.ShowChangePasswordDialog) },
                    onLogout = { viewModel.handleIntent(SettingsIntent.Logout) }
                )
            }
        }

        if (state.isLoading && state.userProfile != null) {
            LoadingScreen()
        }

        if (state.showEditDialog) {
            EditProfileDialog(
                currentFullName = state.userProfile?.fullName ?: "",
                currentEmail = state.userProfile?.email ?: "",
                onDismiss = { viewModel.handleIntent(SettingsIntent.DismissEditProfileDialog) },
                onSave = { fullName, email ->
                    viewModel.handleIntent(SettingsIntent.UpdateAccountDetails(fullName, email))
                }
            )
        }

        if (state.showChangePasswordDialog) {
            ChangePasswordDialog(
                onDismiss = { viewModel.handleIntent(SettingsIntent.DismissChangePasswordDialog) },
                onSave = { oldPassword, newPassword ->
                    viewModel.handleIntent(SettingsIntent.ChangePassword(oldPassword, newPassword))
                }
            )
        }
    }
}



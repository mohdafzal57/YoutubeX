package com.mak.youtubex.presentation.main.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mak.youtubex.presentation.main.common.FullScreenLoader
import com.mak.youtubex.presentation.navigation.LocalSnackbarHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current

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
            }
        }
    }

    val avatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onAction(SettingsAction.UpdateAvatar(it)) }
    }

    val coverImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onAction(SettingsAction.UpdateCoverImage(it)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { /* Handle Share action */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Profile"
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Ensures content starts below the TopAppBar
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading && state.userProfile == null -> {
                    FullScreenLoader()
                }

                state.error != null && state.userProfile == null -> {
                    ErrorContent(
                        error = state.error!!,
                        onRetry = { viewModel.onAction(SettingsAction.LoadUserSettings) },
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.userProfile != null -> {
                    SettingsContent(
                        userProfile = state.userProfile!!,
                        onAvatarClick = { avatarLauncher.launch("image/*") },
                        onCoverImageClick = { coverImageLauncher.launch("image/*") },
                        onEditProfile = { viewModel.onAction(SettingsAction.ShowEditSettingsDialog) },
                        onChangePassword = { viewModel.onAction(SettingsAction.ShowChangePasswordDialog) },
                        onLogout = { viewModel.onAction(SettingsAction.Logout) }
                    )
                }
            }

            if (state.showEditDialog) {
                EditProfileDialog(
                    currentFullName = state.userProfile?.fullName ?: "",
                    currentEmail = state.userProfile?.email ?: "",
                    onDismiss = { viewModel.onAction(SettingsAction.DismissEditSettingsDialog) },
                    onSave = { fullName, email ->
                        viewModel.onAction(SettingsAction.UpdateAccountDetails(fullName, email))
                    }
                )
            }

            if (state.showChangePasswordDialog) {
                ChangePasswordDialog(
                    onDismiss = { viewModel.onAction(SettingsAction.DismissChangePasswordDialog) },
                    onSave = { oldPassword, newPassword ->
                        viewModel.onAction(SettingsAction.ChangePassword(oldPassword, newPassword))
                    }
                )
            }
        }
    }
}
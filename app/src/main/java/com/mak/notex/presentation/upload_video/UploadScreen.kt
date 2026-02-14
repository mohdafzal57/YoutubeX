package com.mak.notex.presentation.upload_video

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mak.notex.utils.Permission
import com.mak.notex.utils.PermissionGate

@Composable
fun UploadScreen(
    viewModel: UploadViewModel = hiltViewModel(),
    navigateToUploadDetail: (Uri) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    PermissionGate(
        permission = Permission.CAMERA,
        contentNonGranted = { missing, humanReadable, requestPermissions ->
            PermissionNonGrantedContent(
                permissionsNonGranted = missing,
                humanReadablePermissionsNonGranted = humanReadable,
                requestMissingPermissions = requestPermissions
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main content based on selected mode
            when (uiState.contentMode) {
                ContentCreationMode.Video, ContentCreationMode.Short -> {
                    CaptureScreen(
                        navigateToUploadDetail = navigateToUploadDetail
                    )
                }

                ContentCreationMode.Post -> {
                    PostCreationScreen(
                        postState = uiState.postState,
                        onTextChange = { viewModel.updatePostText(it) },
                        onVisibilityChange = { viewModel.updatePostVisibility(it) },
                        onClose = { viewModel.closeContent() },
                        onPost = { viewModel.submitPost() }
                    )
                }
            }
        }
    }

    // Action bottom sheet
    ActionBottomSheet(
        isExpanded = uiState.isBottomSheetExpanded,
        onDismiss = { viewModel.closeBottomSheet() },
        onActionSelected = { mode -> viewModel.onActionSelected(mode) }
    )
}

@Composable
private fun PermissionNonGrantedContent(
    modifier: Modifier = Modifier,
    permissionsNonGranted: List<String>,
    humanReadablePermissionsNonGranted: String,
    requestMissingPermissions: (List<String>) -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = buildString {
                append("This screen needs: ")
                append(humanReadablePermissionsNonGranted)
                append(".")
            },
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { requestMissingPermissions(permissionsNonGranted) }) {
            Text(
                if (permissionsNonGranted.size == 1) "Grant $humanReadablePermissionsNonGranted"
                else "Grant Permissions"
            )
        }
    }
}

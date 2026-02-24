package com.mak.notex.presentation.upload_video

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mak.notex.presentation.main.common.AppScaffold
import com.mak.notex.presentation.navigation.LocalSnackbarHostState

@Composable
fun UploadVideoDetailScreen(
    viewModel: UploadVideoDetailViewModel = hiltViewModel(),
    onCancel: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val thumbnailPicker = rememberThumbnailPicker {
        viewModel.onThumbnailChange(it)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                UploadEvent.NavigateHome -> onNavigateToHome()
                is UploadEvent.ShowError -> {
//                    snackbarHostState.showSnackbar(
//                        message = it.message,
//                        duration = SnackbarDuration.Short
//                    )
                }
            }
        }
    }
    AppScaffold(
        title = "Upload Video",
        onBackClick = onCancel,
        showBackButton = true
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                //   Thumbnail
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray)
                        .clickable(enabled = !uiState.isLoading) {
                            thumbnailPicker.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.thumbnailUri != null) {
                        AsyncImage(
                            model = uiState.thumbnailUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Text("Add Thumbnail", color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                Spacer(Modifier.height(12.dp))

                // 📝 Description
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    enabled = !uiState.isLoading
                )
            }

            //  Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = { viewModel.onUpload() },
                    modifier = Modifier.weight(1f),
                    enabled = uiState.title.isNotBlank() && !uiState.isLoading
                ) {
                    Text("Upload")
                }
            }
        }

    }
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}


@Composable
fun rememberThumbnailPicker(
    onPicked: (Uri) -> Unit
) = rememberLauncherForActivityResult(
    ActivityResultContracts.GetContent()
) { uri ->
    uri?.let(onPicked)
}

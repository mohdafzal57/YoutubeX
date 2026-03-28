package com.mak.youtubex.presentation.upload_video

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.telecom.VideoProfile.isPaused
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.ImageCapture
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mak.youtubex.utils.Permission
import com.mak.youtubex.utils.PermissionGate

@Composable
fun CaptureScreen(
    modifier: Modifier = Modifier,
    vm: CameraViewModel,
    navigateToUploadDetail: (Uri) -> Unit,
    onRecording: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        vm.bindCapture(lifecycleOwner)
    }

    DisposableEffect(Unit) {
        onDispose { vm.releaseCamera() }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        uri?.let(navigateToUploadDetail)
    }

    val request by vm.surfaceRequest.collectAsStateWithLifecycle(null)
    val recordingState by vm.recordingUiState.collectAsStateWithLifecycle()
    val recordedVideoUri by vm.videoRecordingUri.collectAsStateWithLifecycle()
    val flashMode by vm.flashMode.collectAsStateWithLifecycle()

    val isRecording = recordingState !is RecordingUiState.Idle

    LaunchedEffect(recordedVideoUri) {
        recordedVideoUri?.let {
            navigateToUploadDetail(it)
            vm.clearVideoUri()
        }
    }

    LaunchedEffect(isRecording) {
        onRecording(isRecording)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        request?.let { req ->
            CameraXViewfinder(
                surfaceRequest = req,
                modifier = Modifier.fillMaxSize()
            )
        }

        /* -------- Side Controls -------- */

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CaptureSideButton(
                icon = Icons.Default.Cameraswitch,
                label = "Flip",
                onClick = { vm.toggleCamera() }
            )

            CaptureSideButton(
                icon = if (flashMode == ImageCapture.FLASH_MODE_ON)
                    Icons.Default.FlashOn
                else
                    Icons.Default.FlashOff,
                label = "Flash",
                onClick = { vm.toggleFlash() }
            )
        }

        /* -------- Bottom Controls -------- */

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT SLOT
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (!isRecording) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                            .clickable {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.VideoOnly
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // CENTER SLOT (always perfectly centered)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                PermissionGate(permission = Permission.RECORD_AUDIO) {
                    RecordingButton(
                        state = recordingState,
                        onClick = {
                            val micGranted =
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                            if (micGranted) {
                                vm.toggleRecording()
                            }
                        }
                    )
                }
            }

            // RIGHT SLOT
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (isRecording) {
                    TextButton(
                        onClick = { vm.stopRecording() }
                    ) {
                        Text(
                            text = "Done",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CaptureSideButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.4f),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun RecordingButton(
    state: RecordingUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val transition = updateTransition(
        targetState = state,
        label = "recording_transition"
    )

    val innerSize by transition.animateDp(label = "inner_size") { s ->
        when (s) {
            RecordingUiState.Idle -> 60.dp
            RecordingUiState.Paused -> 48.dp
            RecordingUiState.Recording -> 32.dp
        }
    }

    val outerSize by transition.animateDp(label = "outer_size") { s ->
        when (s) {
            RecordingUiState.Idle -> 76.dp
            else -> 88.dp
        }
    }

    val corner by transition.animateDp(label = "corner") { s ->
        when (s) {
            RecordingUiState.Recording -> 8.dp
            else -> 30.dp
        }
    }

    val borderColor by transition.animateColor(label = "border") { s ->
        if (s == RecordingUiState.Recording) Color.Red else Color.White
    }

    val innerColor by transition.animateColor(label = "inner_color") { s ->
        Color.Red // can evolve later (pulse, gradient, etc.)
    }

    Box(
        modifier = modifier
            .size(90.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        // Outer Ring
        Box(
            modifier = Modifier
                .size(outerSize)
                .border(4.dp, borderColor, CircleShape)
        )

        // Inner Core
        Box(
            modifier = Modifier
                .size(innerSize)
                .background(innerColor, RoundedCornerShape(corner))
        )
    }
}
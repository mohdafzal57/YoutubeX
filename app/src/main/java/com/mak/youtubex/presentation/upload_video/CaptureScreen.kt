package com.mak.youtubex.presentation.upload_video

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.ImageCapture
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
    onRecording: (Boolean) -> Unit,
    onBackClick: () -> Unit
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
    val recordingState by vm.recordingState.collectAsStateWithLifecycle()
    val recordedVideoUri by vm.videoRecordingUri.collectAsStateWithLifecycle()
    val flashMode by vm.flashMode.collectAsStateWithLifecycle()

    val isRecording = recordingState is CameraViewModel.RecordingState.Active
    val isPaused =
        (recordingState as? CameraViewModel.RecordingState.Active)?.isPaused == true

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
                        isRecording = isRecording,
                        isPaused = isPaused,
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
    isRecording: Boolean,
    isPaused: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val innerSize by animateDpAsState(
        targetValue = when {
            !isRecording -> 60.dp
            isPaused -> 48.dp
            else -> 32.dp
        },
        label = "inner"
    )

    val outerSize by animateDpAsState(
        targetValue = if (isRecording) 88.dp else 76.dp,
        label = "outer"
    )

    val cornerSize by animateDpAsState(
        targetValue = when {
            !isRecording -> 30.dp          // circle
            isPaused -> 30.dp             // circle (resume state)
            else -> 8.dp                  // square (recording state)
        },
        label = "corner"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isRecording) Color.Red else Color.White,
        label = "borderColor"
    )

    val innerColor by animateColorAsState(
        targetValue = when {
            !isRecording -> Color.Red
            isPaused -> Color.Red       // paused = white resume indicator
            else -> Color.Red             // active recording
        },
        label = "innerColor"
    )

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

        // Outer ring
        Box(
            modifier = Modifier
                .size(outerSize)
                .border(4.dp, borderColor, CircleShape)
        )

        // Inner core
        Box(
            modifier = Modifier
                .size(innerSize)
                .background(innerColor, RoundedCornerShape(cornerSize))
        )
    }
}
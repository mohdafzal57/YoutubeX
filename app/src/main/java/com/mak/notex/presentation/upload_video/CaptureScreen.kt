package com.mak.notex.presentation.upload_video

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mak.notex.utils.Permission
import com.mak.notex.utils.PermissionGate

@Composable
fun CaptureScreen(
    vm: CameraViewModel = hiltViewModel(),
    navigateToUploadDetail: (Uri) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Bind all three use cases for this screen.
    LaunchedEffect(Unit) { vm.bindCapture(lifecycleOwner) }

    val request by vm.surfaceRequest.collectAsStateWithLifecycle(null)
    val recording by vm.recording.collectAsStateWithLifecycle()
    val recordedVideoUri by vm.videoRecordingUri.collectAsStateWithLifecycle()

    LaunchedEffect(recordedVideoUri) {
        recordedVideoUri?.let { navigateToUploadDetail(it) }
    }

    Box(Modifier.fillMaxSize()) {
        request?.let { req ->
            CameraXViewfinder(surfaceRequest = req, modifier = Modifier.fillMaxSize())
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(130.dp)
                .padding(bottom = 24.dp)
        ) {
//            Button(onClick = { vm.capturePhoto() }) {
//                Text("Take Photo")
//            }

            // Ask for mic only when the user initiates recording.
            // NOTE: Lint cannot infer PermissionGate's guarantee, so we add an explicit
            // checkSelfPermission here before calling a @RequiresPermission method.
            PermissionGate(permission = Permission.RECORD_AUDIO) {
                Button(
                    onClick = {
                        val micGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (micGranted) {
                            vm.toggleRecording()
                        } else {
                            // Should not happen because PermissionGate only renders this slot
                            // when permission is granted — but guarding keeps Lint happy and
                            // prevents accidental crashes if the state changes.
                        }
                    },
                    modifier = Modifier.align(Alignment.Center),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    )
                ) {
//                    Text(if (recording == null) "Record" else "Stop")
                    RecordingButtonWithAnimation(
                        isRecording = recording != null
                    )
                }
            }
        }
    }
}

@Composable
fun RecordingButtonWithAnimation(
    isRecording: Boolean,
    modifier: Modifier = Modifier,
) {
    val size: Dp by animateDpAsState(if (isRecording) 34.dp else 54.dp)
    val borderSize: Dp by animateDpAsState(if (isRecording) 74.dp else 70.dp)
    val cornerSize: Dp by animateDpAsState(if (isRecording) 8.dp else 64.dp)
    val animatedColor by animateColorAsState(
        targetValue = if (isRecording) Color.Red else Color.White,
        label = "colorAnim"
    )
    Box(
        modifier = Modifier.size(borderSize)
            .border(4.dp, animatedColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(cornerSize))
                .background(Color.Red),
        )
    }
}
package com.mak.notex.presentation.upload

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mak.notex.presentation.upload_video.CameraViewModel
import com.mak.notex.presentation.upload_video.CaptureScreen
import com.mak.notex.utils.Permission
import com.mak.notex.utils.PermissionGate

@Composable
fun ShortScreen(
    cameraViewModel: CameraViewModel = hiltViewModel(),
    navigateToUploadDetail: (Uri) -> Unit,
    onRecording: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
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
        CaptureScreen(
            vm = cameraViewModel,
            navigateToUploadDetail = navigateToUploadDetail,
            modifier = Modifier.fillMaxSize(),
            onRecording = onRecording,
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun PermissionNonGrantedContent(
    permissionsNonGranted: List<String>,
    humanReadablePermissionsNonGranted: String,
    requestMissingPermissions: (List<String>) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Camera access is needed",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Grant permissions to start creating content: $humanReadablePermissionsNonGranted",
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = { requestMissingPermissions(permissionsNonGranted) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
            shape = CircleShape,
            modifier = Modifier.height(50.dp).fillMaxWidth(0.8f)
        ) {
            Text("Grant Permissions", fontWeight = FontWeight.Bold)
        }
    }
}

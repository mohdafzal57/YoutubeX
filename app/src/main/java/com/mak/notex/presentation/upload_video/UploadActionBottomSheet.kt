package com.mak.notex.presentation.upload_video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBottomSheet(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    onActionSelected: (ContentCreationMode) -> Unit
) {
    if (isExpanded) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Create",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                ActionItem(
                    icon = Icons.Default.Videocam,
                    title = "Video",
                    description = "Record a video",
                    onClick = {
                        onActionSelected(ContentCreationMode.Video)
                        onDismiss()
                    }
                )

                ActionItem(
                    icon = Icons.Default.PlayCircle,
                    title = "Short",
                    description = "Create a short video",
                    onClick = {
                        onActionSelected(ContentCreationMode.Short)
                        onDismiss()
                    }
                )

                ActionItem(
                    icon = Icons.Default.Edit,
                    title = "Post",
                    description = "Create a text post",
                    onClick = {
                        onActionSelected(ContentCreationMode.Post)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
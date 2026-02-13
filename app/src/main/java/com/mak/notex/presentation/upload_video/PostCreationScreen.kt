package com.mak.notex.presentation.upload_video

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCreationScreen(
    postState: PostState,
    onTextChange: (String) -> Unit,
    onVisibilityChange: (PostVisibility) -> Unit,
    onClose: () -> Unit,
    onPost: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create post") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, "Close")
                    }
                },
                actions = {
                    Button(
                        onClick = onPost,
                        enabled = postState.text.isNotBlank()
                    ) {
                        Text("Post")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // User info row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "User Name",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Visibility selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = when (postState.visibility) {
                                PostVisibility.PUBLIC -> Icons.Default.Public
                                PostVisibility.PRIVATE -> Icons.Default.Lock
                                PostVisibility.FOLLOWERS -> Icons.Default.People
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = postState.visibility.name.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Text input
            OutlinedTextField(
                value = postState.text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = { Text("Share an image to start a caption contest") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // Bottom actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    IconButton(onClick = { /* Add image */ }) {
                        Icon(Icons.Default.Image, "Add Image")
                    }
                    IconButton(onClick = { /* Add emoji */ }) {
                        Icon(Icons.Default.EmojiEmotions, "Add Emoji")
                    }
                }

                IconButton(onClick = { /* More options */ }) {
                    Icon(Icons.Default.MoreVert, "More")
                }
            }
        }
    }
}
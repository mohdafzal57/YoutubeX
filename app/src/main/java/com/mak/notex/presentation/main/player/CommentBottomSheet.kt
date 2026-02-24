package com.mak.notex.presentation.main.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        CommentInputContent(
            commentText = commentText,
            onCommentTextChange = { commentText = it },
            onSubmitClick = {
                onSubmit(commentText)
                commentText = ""
            }
        )
    }
}

@Composable
private fun CommentInputContent(
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onSubmitClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        CommentInputHeader()
        CommentTextField(
            value = commentText,
            onValueChange = onCommentTextChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        SubmitButton(
            enabled = commentText.isNotBlank(),
            onClick = onSubmitClick
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun CommentInputHeader() {
    Text(
        text = "Add Comment",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun CommentTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Write a comment...") },
        maxLines = 4
    )
}

@Composable
private fun SubmitButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    ) {
        Text("Post")
    }
}
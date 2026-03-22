package com.mak.youtubex.presentation.upload.video_picker

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.mak.youtubex.domain.model.LocalVideo
import com.mak.youtubex.domain.model.VideoFolder
import com.mak.youtubex.utils.Permission
import com.mak.youtubex.utils.PermissionGate
import com.mak.youtubex.utils.formatDuration

@Composable
fun VideoPickerScreen(
    viewModel: LocalVideoPickerViewModel = hiltViewModel(),
    onCloseClick: () -> Unit,
    onVideoClick: (Uri) -> Unit
) {
    PermissionGate(
        permission = Permission.READ_VIDEO,
        contentNonGranted = { missing, humanReadable, requestPermissions ->
            VideoPermissionContent(
                permissionsNonGranted = missing,
                humanReadablePermissionsNonGranted = humanReadable,
                requestMissingPermissions = requestPermissions,
                onCloseClick = onCloseClick
            )
        }
    ) {
        VideoPickerContent(
            viewModel = viewModel,
            onCloseClick = onCloseClick,
            onVideoClick = onVideoClick
        )
    }
}

@Composable
private fun VideoPickerContent(
    viewModel: LocalVideoPickerViewModel,
    onCloseClick: () -> Unit,
    onVideoClick: (Uri) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()

    Scaffold(
        topBar = {
            VideoTopBar(
                onCloseClick = onCloseClick,
                selectedFolder = uiState.selectedFolder,
                folders = uiState.videoFolders,
                onFolderSelected = viewModel::selectFolder
            )
        }
    ) { padding ->

        if (uiState.localVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No videos found")
            }
        } else {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(
                    items = uiState.localVideos,
                    key = { it.id },
                    contentType = { "video" }
                ) { video ->
                    VideoGridItem(video = video, onVideoClick = onVideoClick)
                }
            }
        }
    }
}


@Composable
fun VideoGridItem(video: LocalVideo, onVideoClick: (Uri) -> Unit) {
    val context = LocalContext.current
    val cacheKey = "video_thumb_${video.id}" // Must match the prefetcher exactly

    val model = remember(video.id) {
        ImageRequest.Builder(context)
            .data(video)
            .size(256) // MATCH the prefetcher
            .precision(Precision.EXACT)
            .memoryCacheKey(cacheKey) // MATCH the prefetcher
            .diskCacheKey(cacheKey)
            .crossfade(false) // Keep disabled for instant pop-in
            .build()
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color.DarkGray)
            .clickable { onVideoClick(video.uri) }
    ) {
        AsyncImage(
            model = model,
            contentDescription = video.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Duration overlay
        val durationText = remember(video.duration) {
            formatDuration(video.duration / 1000.0)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = durationText,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoTopBar(
    selectedFolder: VideoFolder?,
    folders: List<VideoFolder>,
    onFolderSelected: (VideoFolder?) -> Unit,
    onCloseClick: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedFolder?.name ?: "Videos",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(4.dp))

                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("All Videos")
                            if (selectedFolder == null) {
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        }
                    },
                    onClick = {
                        onFolderSelected(null)
                        expanded = false
                    }
                )

                HorizontalDivider()

                folders.forEach { folder ->
                    key(folder.id) {
                        FolderDropdownItem(
                            folder = folder,
                            isSelected = selectedFolder?.id == folder.id,
                            onClick = {
                                onFolderSelected(folder)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        IconButton(onClick = onCloseClick) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }
    }
}


@Composable
private fun FolderDropdownItem(
    folder: VideoFolder,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        onClick = onClick,
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                AsyncImage(
                    model = folder.thumbnailUri,
                    contentDescription = folder.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${folder.videoCount} videos",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    )
}

@Composable
private fun VideoPermissionContent(
    permissionsNonGranted: List<String>,
    humanReadablePermissionsNonGranted: String,
    requestMissingPermissions: (List<String>) -> Unit,
    onCloseClick: () -> Unit
) {
    Scaffold(
        topBar = { /*VideoTopBar(onCloseClick = onCloseClick)*/ }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.VideoLibrary,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Video access required",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Grant $humanReadablePermissionsNonGranted permission to display your videos.",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { requestMissingPermissions(permissionsNonGranted) },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Grant Permission")
            }
        }
    }
}

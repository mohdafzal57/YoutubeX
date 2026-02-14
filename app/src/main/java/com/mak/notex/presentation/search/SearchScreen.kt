package com.mak.notex.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mak.notex.domain.model.UserVideo
import com.mak.notex.domain.model.VideoFeed
import com.mak.notex.presentation.common.VideoItem
import com.mak.notex.presentation.home.BottomLoader
import com.mak.notex.presentation.home.FullScreenLoader
import com.mak.notex.presentation.home.RetryFooter
import java.util.Locale

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onVideoClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToChannel: (String, String) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResultUiState by viewModel.searchResultUiState.collectAsStateWithLifecycle()
    val videos = viewModel.videos.collectAsLazyPagingItems()

    SearchScreen(
        modifier = modifier,
        searchQuery = searchQuery,
        searchResultUiState = searchResultUiState,
        videos = videos,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onBackClick = onBackClick,
        onVideoClick = onVideoClick,
        onNavigateToChannel = onNavigateToChannel
    )
}

@Composable
internal fun SearchScreen(
    searchQuery: String,
    searchResultUiState: SearchResultUiState,
    videos: LazyPagingItems<VideoFeed>,
    onSearchQueryChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onVideoClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToChannel: (String, String) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        SearchToolbar(
            onBackClick = onBackClick,
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchTriggered = { /* Debounced in ViewModel */ },
            searchQuery = searchQuery,
        )
        Box(modifier = Modifier.weight(1f)) {
            when (searchResultUiState) {
                is SearchResultUiState.Success -> {
                    if (videos.itemCount == 0 && videos.loadState.refresh is LoadState.NotLoading) {
                        EmptySearchResultBody(searchQuery = searchQuery)
                    } else {
                        SearchResultBody(
                            videos = videos,
                            onVideoClick = onVideoClick,
                            onNavigateToChannel = onNavigateToChannel
                        )
                    }
                }
                SearchResultUiState.EmptyQuery -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Enter at least 2 characters to search", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                SearchResultUiState.LoadFailed -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Failed to load results")
                    }
                }
                SearchResultUiState.Loading -> {
                    FullScreenLoader()
                }
                SearchResultUiState.SearchNotReady -> {
                    SearchNotReadyBody()
                }
            }
        }
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
    }
}

@Composable
private fun SearchResultBody(
    videos: LazyPagingItems<VideoFeed>,
    onVideoClick: (String, String) -> Unit,
    onNavigateToChannel: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(
            count = videos.itemCount,
            key = videos.itemKey { it.id }
        ) { index ->
            videos[index]?.let { video ->
                VideoItem(
                    video = video,
                    onClick = { onVideoClick(video.videoFile, video.id) },
                    onNavigateToChannel = {onNavigateToChannel(video.username, video.ownerId)}
                )
            }
        }

        when (videos.loadState.append) {
            is LoadState.Loading -> {
                item { BottomLoader() }
            }
            is LoadState.Error -> {
                item { RetryFooter { videos.retry() } }
            }
            else -> {}
        }
    }
}

@Composable
fun EmptySearchResultBody(
    searchQuery: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
    ) {
        val message = "Result for \"$searchQuery\" not found"
        val start = message.indexOf(searchQuery)
        Text(
            text = if (start != -1) {
                AnnotatedString(
                    text = message,
                    spanStyles = listOf(
                        AnnotatedString.Range(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            start = start,
                            end = start + searchQuery.length,
                        ),
                    ),
                )
            } else {
                AnnotatedString(message)
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp),
        )
    }
}

@Composable
private fun SearchNotReadyBody() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Search not ready",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchToolbar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        SearchTextField(
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchTriggered = onSearchTriggered,
            searchQuery = searchQuery,
        )
    }
}

@Composable
private fun SearchTextField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val onSearchExplicitlyTriggered = {
        keyboardController?.hide()
        onSearchTriggered(searchQuery)
    }

    TextField(
        value = searchQuery,
        onValueChange = {
            if ("\n" !in it) onSearchQueryChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    if (searchQuery.isBlank()) return@onKeyEvent false
                    onSearchExplicitlyTriggered()
                    true
                } else {
                    false
                }
            }
            .testTag("searchTextField"),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search_icon",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onSearchQueryChanged("")
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "clear_serach_text",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        placeholder = { Text("Search videos...") },
        shape = RoundedCornerShape(32.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                if (searchQuery.isBlank()) return@KeyboardActions
                onSearchExplicitlyTriggered()
            },
        ),
        maxLines = 1,
        singleLine = true,
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


package com.mak.youtubex.presentation.main.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mak.youtubex.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YootubeTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavigateToSearch: () -> Unit = {}
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        modifier = modifier,
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface, // Standard color
            scrolledContainerColor = MaterialTheme.colorScheme.surface // Keep it the same when scrolled
        ),
        title = { },
        navigationIcon = {
            YooTubeLogo()
        },
        actions = { VideoSearchIcon(onClick = onNavigateToSearch) }
    )
}
@Preview(showBackground = true)
@Composable
fun YooTubeLogo(
    modifier: Modifier = Modifier
) {
    val contentColor = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier
            .padding(start = 12.dp)
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.yt_ic),
            contentDescription = "YooTube logo",
            modifier = Modifier.size(36.dp)
        )

        Image(
            painter = painterResource(R.drawable.yt_txt),
            contentDescription = "YooTube text",
            modifier = Modifier.height(24.dp),
            colorFilter = ColorFilter.tint(contentColor)
        )
    }
}

@Composable
fun VideoSearchIcon(
    onClick: () -> Unit = {}
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

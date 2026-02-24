package com.mak.notex.presentation.main.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mak.notex.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YootubeTopAppBar(
    modifier: Modifier = Modifier,
    onNavigateToSearch: () -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
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
            painter = painterResource(R.drawable.topbar_icon),
            contentDescription = "YooTube logo",
            modifier = Modifier.size(36.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Image(
            painter = painterResource(R.drawable.youtube_text_ic),
            contentDescription = "YooTube text",
            modifier = Modifier.height(22.dp),
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

package com.mak.youtubex.presentation.main.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mak.youtubex.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YTTopAppBar(
    title: String = "",
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    scrolledContainerColor: Color = MaterialTheme.colorScheme.surface
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor,
            scrolledContainerColor = scrolledContainerColor
        ),
        scrollBehavior = scrollBehavior
    )
}


@Preview(showBackground = true)
@Composable
fun YTLogo(
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

@Composable
fun YTBackButton(
    onBackClick: () -> Unit = {},
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(onClick = onBackClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = contentColor
        )
    }
}
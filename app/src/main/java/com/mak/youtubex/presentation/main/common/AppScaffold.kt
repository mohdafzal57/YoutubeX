package com.mak.youtubex.presentation.main.common

import android.R.attr.contentDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mak.youtubex.R
import com.mak.youtubex.presentation.ui.theme.YTTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    showActions: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable (() -> Unit) = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = contentColor
                            )
                        }
                    }
                },
                actions = { if (showActions) actions() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = containerColor,
                    titleContentColor = contentColor,
                    navigationIconContentColor = contentColor,
                    actionIconContentColor = contentColor
                )
            )
        },
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        bottomBar = bottomBar,
        containerColor = containerColor,
        contentColor = contentColor,
        content = content
    )
}

// shared/components/ErrorScreen.kt
@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.error_title),
    icon: Painter? = null,
    retryText: String = stringResource(R.string.retry),
    isRetrying: Boolean = false
) {
    CenteredContent(
        modifier = modifier
    ) {

        // Icon
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = icon,
                    contentDescription = stringResource(R.string.error_icon_desc),
                    modifier = Modifier.size(300.dp)
                )
            }

            Spacer(Modifier.height(Dimens.spaceXl))
        }

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(Modifier.height(Dimens.spaceSm))

        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(Modifier.height(Dimens.spaceXl))

        // Retry Button
        PrimaryButton(
            text = retryText,
            onClick = onRetry,
            isLoading = isRetrying,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}

// shared/components/EmptyState.kt
@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    painter: Painter? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    CenteredContent(
        modifier = modifier
    ) {
        // Icon
        when {
            icon != null -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.iconLg),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }

            painter != null -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.iconLg)
                )
            }
        }

        Spacer(Modifier.height(Dimens.spaceMd))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(Dimens.spaceSm))

        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        // Optional Action
        if (actionText != null && onAction != null) {
            Spacer(Modifier.height(Dimens.spaceLg))

            PrimaryButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}

// design/Dimens.kt
object Dimens {
    val spaceXs = 4.dp
    val spaceSm = 8.dp
    val spaceMd = 16.dp
    val spaceLg = 24.dp
    val spaceXl = 32.dp

    val iconLg = 80.dp
    val iconContainer = 120.dp

    val buttonHeight = 48.dp
}

// shared/components/PrimaryButton.kt
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.height(Dimens.buttonHeight)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// shared/components/CenteredContent.kt
@Composable
fun CenteredContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.spaceXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun AppScaffoldPreview() {
    YTTheme {
        AppScaffold(
            title = "YouTubeX",
            showBackButton = true,
            onBackClick = {},
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Content Area")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    YTTheme {
        ErrorScreen(
            message = "Unable to load data. Please check your internet connection.",
            onRetry = {},
            icon = painterResource(id = R.drawable.offline_dino_car)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    YTTheme {
        EmptyState(
            title = "No videos found",
            message = "Try searching for something else or check your internet connection.",
            icon = Icons.Rounded.WifiOff,
            actionText = "Refresh",
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    YTTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrimaryButton(text = "Primary Button", onClick = {})
            PrimaryButton(text = "Loading Button", onClick = {}, isLoading = true)
            PrimaryButton(text = "Disabled Button", onClick = {}, enabled = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CenteredContentPreview() {
    YTTheme {
        CenteredContent {
            Text(text = "Centered Content", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "This content is centered both horizontally and vertically.")
        }
    }
}
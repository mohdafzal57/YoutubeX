package com.mak.notex.presentation.ui.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = YouTubeRed,
    onPrimary = Color.White,

    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,

    secondary = LightSecondary,
    onSecondary = Color.White,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,

    tertiary = YouTubeBlue,
    onTertiary = Color.White,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,

    background = LightBackground,
    onBackground = LightOnBackground,

    surface = LightBackground,
    onSurface = LightOnBackground,

    surfaceVariant = LightFieldContainer,        // ← YOUR GEM
    onSurfaceVariant = LightFieldHint,           // ← YOUR GEM

    outline = LightFieldBorder                  // ← YOUR GEM
)

private val DarkColorScheme = darkColorScheme(
    primary = YouTubeRed,
    onPrimary = Color.White,

    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = LightPrimaryContainer,

    secondary = DarkSecondary,
    onSecondary = LightOnBackground,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = LightSecondaryContainer,

    tertiary = YouTubeBlueDark,
    onTertiary = LightOnTertiaryContainer,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = LightTertiaryContainer,

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnBackground,

    surfaceVariant = DarkFieldContainer,        // ← YOUR GEM
    onSurfaceVariant = DarkFieldHint,           // ← YOUR GEM

    outline = DarkFieldBorder                   // ← YOUR GEM
)

@Composable
fun NoteXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && supportsDynamicTheming() -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
private fun supportsDynamicTheming(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

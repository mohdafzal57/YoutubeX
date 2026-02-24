package com.mak.notex.presentation.ui.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(

    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF7F0018),
    onPrimaryContainer = BrandPrimaryContainer,

    secondary = AccentBlue,
    onSecondary = Color.Black,
    secondaryContainer = AccentBlueContainer,
    onSecondaryContainer = Color(0xFFD0E4FF),

    background = Neutral0,
    onBackground = Neutral90,

    surface = Neutral10,
    onSurface = Neutral90,

    surfaceVariant = Neutral30,
    onSurfaceVariant = Neutral70,

    outline = Neutral40,

    error = ErrorDark,
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(

    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = BrandOnPrimaryContainer,

    secondary = AccentBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6E3FF),
    onSecondaryContainer = Color(0xFF001B3D),

    background = LightNeutral0,
    onBackground = LightNeutral90,

    surface = LightNeutral0,
    onSurface = LightNeutral90,

    surfaceVariant = LightNeutral20,
    onSurfaceVariant = LightNeutral60,

    outline = LightNeutral40,

    error = ErrorLight,
    onError = Color.White
)

//@Composable
//fun YouTubeTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    content: @Composable () -> Unit
//) {
//    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
//    val view = LocalView.current
//
//    // Industry Standard: Controlling System Bar colors
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            window.statusBarColor = colorScheme.background.toArgb()
//            window.navigationBarColor = colorScheme.background.toArgb()
//
//            // Set light/dark icons based on theme
//            WindowCompat.getInsetsController(window, view).apply {
//                isAppearanceLightStatusBars = !darkTheme
//                isAppearanceLightNavigationBars = !darkTheme
//            }
//        }
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography, // Ensure you have defined your M3 Typography
//        content = content
//    )
//}
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

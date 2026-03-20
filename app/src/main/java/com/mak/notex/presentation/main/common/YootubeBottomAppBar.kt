package com.mak.notex.presentation.main.common

import android.app.Activity
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mak.notex.R
import com.mak.notex.presentation.navigation.Screen
import com.mak.notex.presentation.upload.UploadActivity

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector
)

val TOP_LEVEL_DESTINATIONS = mapOf(
    Screen.Home.route to BottomNavigationItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    ),
    Screen.SocialFeed.route to BottomNavigationItem(
        title = "Posts",
        selectedIcon = Icons.Filled.Image,
        unSelectedIcon = Icons.Outlined.Image
    ),
    Screen.Short.route to BottomNavigationItem(
        title = "Short",
        selectedIcon = Icons.Filled.Add,
        unSelectedIcon = Icons.Outlined.Add
    ),
    Screen.Subscription.route to BottomNavigationItem(
        title = "Subscriptions",
        selectedIcon = Icons.Filled.Subscriptions,
        unSelectedIcon = Icons.Outlined.Subscriptions
    ),
    Screen.Settings.route to BottomNavigationItem(
        title = "You",
        selectedIcon = Icons.Filled.AccountCircle,
        unSelectedIcon = Icons.Outlined.AccountCircle
    )
)

@Composable
fun YTNavigationBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    ShortNavigationBar(
        modifier = modifier,
        containerColor = YTNavigationDefaults.containerColor()
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (destination, item) ->
            val isSelected = currentRoute?.startsWith(destination) == true
            val context = LocalContext.current
            if (destination == Screen.Short.route) {
                CompositionLocalProvider(LocalRippleConfiguration provides null) {
                    ShortNavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            val activity = context as? Activity
                            val intent = Intent(context, UploadActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

                            context.startActivity(intent)

                            activity?.let {
                                if (Build.VERSION.SDK_INT >= 34) {
                                    it.overrideActivityTransition(
                                        Activity.OVERRIDE_TRANSITION_OPEN,
                                        R.anim.slide_up,
                                        R.anim.stay
                                    )
                                } else {
                                    @Suppress("DEPRECATION")
                                    it.overridePendingTransition(
                                        R.anim.slide_up,
                                        R.anim.stay
                                    )
                                }
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = MaterialTheme.shapes.extraLarge
                                    )
                                    .size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unSelectedIcon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        },
                        label = {},
                        colors = ShortNavigationBarItemDefaults.colors(
                            selectedIconColor = YTNavigationDefaults.selectedItemColor(),
                            unselectedIconColor = YTNavigationDefaults.unselectedItemColor(),
                            selectedTextColor = YTNavigationDefaults.selectedItemColor(),
                            unselectedTextColor = YTNavigationDefaults.unselectedItemColor(),
                        )
                    )
                }
            } else {
                ShortNavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        onNavigate(destination)
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unSelectedIcon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                        )
                    },
                    colors = ShortNavigationBarItemDefaults.colors(
                        selectedIconColor = YTNavigationDefaults.selectedItemColor(),
                        unselectedIconColor = YTNavigationDefaults.unselectedItemColor(),
                        selectedTextColor = YTNavigationDefaults.selectedItemColor(),
                        unselectedTextColor = YTNavigationDefaults.unselectedItemColor(),
                    )
                )
            }
        }
    }
}

object YTNavigationDefaults {

    @Composable
    fun containerColor(): Color =
        MaterialTheme.colorScheme.surface

    @Composable
    fun selectedItemColor(): Color =
        if (MaterialTheme.colorScheme.background.luminance() < 0.5f)
            Color.White
        else
            Color.Black

    @Composable
    fun unselectedItemColor(): Color =
        MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun indicatorColor(): Color =
        Color.Transparent
}

@Composable
fun Modifier.glassEffect(
    blurRadius: Float = 25f,
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
): Modifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    this
        .graphicsLayer {
            // Use .asComposeRenderEffect() to bridge Android Graphics to Compose
            renderEffect = RenderEffect
                .createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP)
                .asComposeRenderEffect()
        }
        .background(backgroundColor)
} else {
    // Fallback for older devices (just background color, no blur)
    this.background(backgroundColor)
}

@Preview
@Composable
private fun YTNavigationBarPreview() {
    /*YTNavigationBar(
        currentRoute = Screen.Home.route,
        onNavigate = {}
    )*/
}
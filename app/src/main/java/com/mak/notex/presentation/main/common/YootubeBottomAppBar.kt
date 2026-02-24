package com.mak.notex.presentation.main.common

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.mak.notex.R
import com.mak.notex.presentation.navigation.Screen
import com.mak.notex.presentation.upload.UploadActivity

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val route: String
)

@Composable
fun YootubeBottomAppBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val bottomNavigationItems = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home,
            route = Screen.Home.route
        ),
        BottomNavigationItem(
            title = "Posts",
            selectedIcon = Icons.Filled.Image,
            unSelectedIcon = Icons.Outlined.Image,
            route = Screen.SocialFeed.route
        ),
        BottomNavigationItem(
            title = "Upload",
            selectedIcon = Icons.Filled.AddCircleOutline,
            unSelectedIcon = Icons.Outlined.AddCircleOutline,
            route = Screen.Short.route
        ),
        BottomNavigationItem(
            title = "Subscriptions",
            selectedIcon = Icons.Filled.Subscriptions,
            unSelectedIcon = Icons.Outlined.Subscriptions,
            route = Screen.Subscription.route
        ),
        BottomNavigationItem(
            title = "You",
            selectedIcon = Icons.Filled.AccountCircle,
            unSelectedIcon = Icons.Outlined.AccountCircle,
            route = Screen.Settings.route
        )
    )

    NavigationBar(
        modifier = modifier,
        containerColor = YTNavigationDefaults.containerColor()
    ) {
        bottomNavigationItems.forEach { item ->
            val isSelected = currentRoute?.startsWith(item.route) == true
            val context = LocalContext.current
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (item.route == Screen.Short.route) {

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

                    } else {
                        onNavigate(item.route)
                    }
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
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = YTNavigationDefaults.selectedItemColor(),
                    unselectedIconColor = YTNavigationDefaults.unselectedItemColor(),
                    selectedTextColor = YTNavigationDefaults.selectedItemColor(),
                    unselectedTextColor = YTNavigationDefaults.unselectedItemColor(),
                    indicatorColor = YTNavigationDefaults.indicatorColor()
                )
            )
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

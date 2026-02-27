package com.mak.notex.presentation.main.common

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YTNavigationBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    scrollBehavior: BottomAppBarScrollBehavior,
    onNavigate: (String) -> Unit
) {
    BottomAppBar(
        modifier = modifier,
        containerColor = YTNavigationDefaults.containerColor(),
        scrollBehavior = scrollBehavior,
        contentPadding = PaddingValues(0.dp) // Remove default padding to let NavigationBar fill it
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            TOP_LEVEL_DESTINATIONS.forEach { (destination, item) ->
                val isSelected = currentRoute?.startsWith(destination) == true
                val context = LocalContext.current
                if (destination == Screen.Short.route) {
                    NavigationBarItem(
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
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = YTNavigationDefaults.selectedItemColor(),
                            unselectedIconColor = YTNavigationDefaults.unselectedItemColor(),
                            selectedTextColor = YTNavigationDefaults.selectedItemColor(),
                            unselectedTextColor = YTNavigationDefaults.unselectedItemColor(),
                            indicatorColor = YTNavigationDefaults.indicatorColor()
                        )
                    )
                } else {
                    NavigationBarItem(
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
    }
}
/*
@Composable
fun YTNavigationBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = YTNavigationDefaults.containerColor()
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (destination, item) ->
            val isSelected = currentRoute?.startsWith(destination) == true
            val context = LocalContext.current
            if (destination == Screen.Short.route) {
                NavigationBarItem(
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
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = YTNavigationDefaults.selectedItemColor(),
                        unselectedIconColor = YTNavigationDefaults.unselectedItemColor(),
                        selectedTextColor = YTNavigationDefaults.selectedItemColor(),
                        unselectedTextColor = YTNavigationDefaults.unselectedItemColor(),
                        indicatorColor = YTNavigationDefaults.indicatorColor()
                    )
                )
            } else {
                NavigationBarItem(
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
}
*/

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

@Preview
@Composable
private fun YTNavigationBarPreview() {
    /*YTNavigationBar(
        currentRoute = Screen.Home.route,
        onNavigate = {}
    )*/
}
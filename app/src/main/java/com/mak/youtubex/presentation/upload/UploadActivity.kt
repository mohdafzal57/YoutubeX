package com.mak.youtubex.presentation.upload

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mak.youtubex.R
import com.mak.youtubex.presentation.navigation.Screen
import com.mak.youtubex.presentation.upload.create_post.PostScreen
import com.mak.youtubex.presentation.upload.ui.theme.YoutubeX
import com.mak.youtubex.presentation.upload.video_picker.VideoPickerScreen
import com.mak.youtubex.presentation.upload_video.ModeSelector
import com.mak.youtubex.presentation.upload_video.UploadVideoDetailScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            YoutubeX(darkTheme = true) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Best Practice: Use the route from Screen object instead of hardcoded title comparison
                val mode = ContentCreationMode.allModes.find { it.title == currentRoute }
                    ?: ContentCreationMode.Short

                val isUploadDetail =
                    currentRoute?.startsWith(Screen.UploadVideoDetail.route.substringBefore("/{")) == true

                val isRecording = remember { mutableStateOf(false) }
                val shouldShowBottomBar = !isUploadDetail && !isRecording.value

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.systemBars,
                    containerColor = Color.Black,
                    bottomBar = {
                        Box(Modifier.consumeWindowInsets(WindowInsets.ime)) {
                            Surface(
                                color = Color.Black,
                                tonalElevation = 8.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                BottomAppBar(containerColor = Color.Transparent) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Crossfade(shouldShowBottomBar, label = "") { visible ->
                                            if (visible) {
                                                ModeSelector(
                                                    selectedMode = mode,
                                                    onModeSelected = { selectedMode ->
                                                        // selectedMode.title should ideally correspond to Screen.X.route
                                                        navController.navigate(selectedMode.title) {
                                                            popUpTo(0) { inclusive = true }
                                                            launchSingleTop = true
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Short.route,
                        modifier = Modifier
                            .padding(paddingValues)
                            .consumeWindowInsets(paddingValues)
                    ) {
                        composable(route = Screen.Short.route) {
                            ShortScreen(
                                navigateToUploadDetail = { uri ->
                                    // BEST PRACTICE: Use the helper function from Screen.kt
                                    navController.navigate(Screen.UploadVideoDetail.createRoute(uri.toString()))
                                },
                                onBackClick = { finish() },
                                onRecording = { isRecording.value = it }
                            )
                        }

                        composable(route = Screen.Video.route) {
                            VideoPickerScreen(
                                onCloseClick = { finish() },
                                onVideoClick = { uri ->
                                    navController.navigate(Screen.UploadVideoDetail.createRoute(uri.toString()))
                                }
                            )
                        }

                        composable(route = Screen.Post.route) {
                            PostScreen(onCloseClick = { finish() })
                        }

                        composable(
                            route = Screen.UploadVideoDetail.route,
                            arguments = listOf(
                                // BEST PRACTICE: Use constant from Screen companion
                                navArgument(Screen.ARG_VIDEO_URI) { type = NavType.StringType }
                            )
                        ) {
                            UploadVideoDetailScreen(
                                onCancel = { navController.popBackStack() },
                                onNavigateToHome = { finish() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.stay,
                R.anim.slide_down
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.stay, R.anim.slide_down)
        }
    }
}

sealed class ContentCreationMode(val title: String) {
    object Video : ContentCreationMode("video")
    object Short : ContentCreationMode("short")
    object Post : ContentCreationMode("post")

    companion object {
        val allModes get() = listOf(Video, Short, Post)
    }
}

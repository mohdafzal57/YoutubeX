package com.mak.notex.presentation.navigation

import com.mak.notex.presentation.subscription.SubscriptionScreen
import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mak.notex.presentation.channel.ChannelScreen
import com.mak.notex.presentation.home.HomeScreen
import com.mak.notex.presentation.player.PlayerScreen
import com.mak.notex.presentation.player.PlayerViewModel
import com.mak.notex.presentation.search.SearchScreen
import com.mak.notex.presentation.settings.SettingsScreen
import com.mak.notex.presentation.tweet.CreateTweetScreen
import com.mak.notex.presentation.ui.theme.NoteXTheme
import com.mak.notex.presentation.upload_video.UploadScreen
import com.mak.notex.presentation.upload_video.UploadVideoDetailScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
    onNavigateToAuth: () -> Unit
) {
    navigation(
        startDestination = Screen.Home.route,
        route = NavGraphs.MAIN
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onPlayVideo = { videoUrl, videoId ->
                    navController.navigate(Screen.Player.createRoute(videoUrl, videoId))
                },
                onNavigateToChannel = { username, ownerId ->
                    navController.navigate(Screen.ChannelDetail.route + "/$username/$ownerId")
                },
            )
        }

        composable(
            route = Screen.UploadVideo.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            UploadScreen(
                navigateToUploadDetail = { uri ->
                    val encodedUri = Uri.encode(uri.toString())
                    navController.navigate(Screen.UploadVideoDetailScreen.route + "/${encodedUri}")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            Screen.UploadVideoDetailScreen.route + "/{videoUri}",
            arguments = listOf(
                navArgument("videoUri") {
                    type = NavType.StringType
                }
            )
        ) {
            UploadVideoDetailScreen(
                onCancel = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.UploadVideoDetailScreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CreateTweet.route) {
            CreateTweetScreen()
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onVideoClick = {
                    videoUrl, videoId ->
                    navController.navigate(Screen.Player.createRoute(videoUrl, videoId))
                },
                onNavigateToChannel = { username, ownerId ->
                    navController.navigate(Screen.ChannelDetail.route + "/$username/$ownerId")
                }
            )
        }

        composable(Screen.Subscription.route) {
            SubscriptionScreen(
                onNavigateToChannel = { username, ownerId ->
                    navController.navigate(Screen.ChannelDetail.route + "/$username/$ownerId")
                }
            )
        }
        composable(
            Screen.ChannelDetail.route + "/{username}/{ownerId}",
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("ownerId") { type = NavType.StringType }
            )
        ) {
            ChannelScreen(
                onNavigateBack = { navController.popBackStack() },
                onPlayVideo = { videoUrl, videoId ->
                    navController.navigate(Screen.Player.createRoute(videoUrl, videoId))
                },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogoutSuccess = onNavigateToAuth
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("encodedUrl") { type = NavType.StringType },
                navArgument("videoId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("encodedUrl") ?: ""
            val url = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
            val viewModel = hiltViewModel<PlayerViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            PlayerScreen(
                videoUrl = url,
                uiState = uiState,
                onEvent = viewModel::onEvent,
            ) {
                navController.popBackStack()
            }
        }
    }
}

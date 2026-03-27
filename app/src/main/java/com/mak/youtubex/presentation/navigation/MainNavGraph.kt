package com.mak.youtubex.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mak.youtubex.presentation.main.channel.ChannelScreen
import com.mak.youtubex.presentation.main.home.HomeScreen
import com.mak.youtubex.presentation.main.player.PlayerScreen
import com.mak.youtubex.presentation.main.player.PlayerViewModel
import com.mak.youtubex.presentation.main.search.SearchScreen
import com.mak.youtubex.presentation.main.settings.ProfileScreen
import com.mak.youtubex.presentation.main.social_feed.SocialFeedScreen
import com.mak.youtubex.presentation.main.subscription.SubscriptionScreen
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
                    // BEST PRACTICE: Use createRoute instead of manual string concatenation
                    navController.navigate(Screen.ChannelDetail.createRoute(username, ownerId))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        composable(Screen.SocialFeed.route) {
            SocialFeedScreen()
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onBackClick = { navController.popBackStack() },
                onVideoClick = { videoUrl, videoId ->
                    navController.navigate(Screen.Player.createRoute(videoUrl, videoId))
                },
                onNavigateToChannel = { username, ownerId ->
                    navController.navigate(Screen.ChannelDetail.createRoute(username, ownerId))
                }
            )
        }

        composable(Screen.Subscription.route) {
            SubscriptionScreen(
                onNavigateToChannel = { username, ownerId ->
                    navController.navigate(Screen.ChannelDetail.createRoute(username, ownerId))
                }
            )
        }

        composable(
            route = Screen.ChannelDetail.route, // BEST PRACTICE: Route logic is now inside Screen.kt
            arguments = listOf(
                navArgument(Screen.ARG_USERNAME) { type = NavType.StringType },
                navArgument(Screen.ARG_OWNER_ID) { type = NavType.StringType }
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
            ProfileScreen(
                onLogoutSuccess = onNavigateToAuth
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument(Screen.ARG_ENCODED_URL) { type = NavType.StringType },
                navArgument(Screen.ARG_VIDEO_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // BEST PRACTICE: Use constants for keys to avoid typos
            val encodedUrl = backStackEntry.arguments?.getString(Screen.ARG_ENCODED_URL) ?: ""
            val url = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())

            val viewModel = hiltViewModel<PlayerViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            PlayerScreen(
                videoUrl = url,
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
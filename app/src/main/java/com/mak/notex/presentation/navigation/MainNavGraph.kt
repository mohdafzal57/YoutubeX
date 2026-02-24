package com.mak.notex.presentation.navigation

import com.mak.notex.presentation.main.subscription.SubscriptionScreen
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mak.notex.presentation.main.channel.ChannelScreen
import com.mak.notex.presentation.main.home.HomeScreen
import com.mak.notex.presentation.main.player.PlayerScreen
import com.mak.notex.presentation.main.player.PlayerViewModel
import com.mak.notex.presentation.main.search.SearchScreen
import com.mak.notex.presentation.main.settings.SettingsScreen
import com.mak.notex.presentation.main.social_feed.SocialFeedScreen
import com.mak.notex.presentation.main.tweet.CreateTweetScreen
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

        composable(Screen.SocialFeed.route) {
            SocialFeedScreen(
                /*onNavigateToChannel = { username, ownerId ->
                    navController.navigate(Screen.ChannelDetail.route + "/$username/$ownerId")
                },*/
            )
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

package com.mak.notex.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    data object SignIn : Screen("sign_in")
    data object SignUp : Screen("sign_up")
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object Search : Screen("search")
    data object SocialFeed : Screen("social_feed")
    data object ChannelDetail : Screen("channel/{username}/{ownerId}")
    data object Subscription : Screen("subscription") // Add this

    data object Player : Screen("player/{videoId}/{encodedUrl}") {
        fun createRoute(url: String, videoId: String): String {
            val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "player/$videoId/$encoded"
        }
    }


    data object Short : Screen("short")
    data object Post : Screen("post")
    data object Video : Screen("video")
    data object UploadVideoDetailScreen : Screen("video_upload_detail/{videoUri}")
}

private const val VIDEO_ID = "videoId"

val TOP_LEVEL_DESTINATIONS = setOf(
    Screen.SocialFeed.route,
    Screen.Home.route,
    Screen.Short.route,
    Screen.Settings.route,
    Screen.Subscription.route
)

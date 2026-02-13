package com.mak.notex.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    data object SignIn : Screen("sign_in")
    data object SignUp : Screen("sign_up")
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object Search : Screen("search")
    data object ChannelDetail : Screen("channel/{username}/{ownerId}")
    data object CreateTweet : Screen("create_tweet")
    data object Subscription : Screen("subscription") // Add this
    data object UploadVideo : Screen("upload_video")
    data object UploadVideoDetailScreen : Screen("video_upload_detail/{videoUri}")


    data object Player : Screen("player/{videoId}/{encodedUrl}") {
        fun createRoute(url: String, videoId: String): String {
            val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "player/$videoId/$encoded"
        }
    }
}
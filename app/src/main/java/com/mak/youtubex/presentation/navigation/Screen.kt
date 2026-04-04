package com.mak.youtubex.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    // Top Level
    data object Home : Screen("home")
    data object SocialFeed : Screen("social_feed")
    data object Short : Screen("short")
    data object Subscription : Screen("subscription")
    data object Settings : Screen("settings")

    // Auth
    data object SignIn : Screen("sign_in")
    data object SignUp : Screen("sign_up")

    // Features
    data object Search : Screen("search")

    data object ChannelDetail : Screen("channel/{$ARG_USERNAME}") {
        fun createRoute(username: String) = "channel/$username"
    }

    data object Player : Screen("player/{$ARG_VIDEO_ID}/{$ARG_ENCODED_URL}") {
        fun createRoute(url: String, videoId: String): String {
            val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "player/$videoId/$encoded"
        }
    }

    data object PostDetail : Screen("post_detail/{$ARG_POST_ID}") {
        fun createRoute(postId: String) = "post_detail/$postId"
    }

    data object UploadVideoDetail : Screen("video_upload_detail/{$ARG_VIDEO_URI}") {
        fun createRoute(videoUri: String): String {
            val encoded = URLEncoder.encode(videoUri, StandardCharsets.UTF_8.toString())
            return "video_upload_detail/$encoded"
        }
    }

    data object Post : Screen("post")
    data object Video : Screen("video")

    companion object {
        const val ARG_VIDEO_ID = "videoId"
        const val ARG_ENCODED_URL = "encodedUrl"
        const val ARG_USERNAME = "username"
        const val ARG_OWNER_ID = "ownerId"
        const val ARG_VIDEO_URI = "videoUri"
        const val ARG_POST_ID = "postId"
    }
}
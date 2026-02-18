package com.mak.notex.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun formatDuration(duration: Double): String {
    val seconds = duration.toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (minutes >= 60) {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        String.format("%d:%02d:%02d", hours, remainingMinutes, remainingSeconds)
    } else {
        String.format("%02d:%02d", minutes, remainingSeconds)
    }
}

fun formatLikesCount(count: Int): String {
    return when {
        count >= 100_000_000 -> "${(count / 1_000_000)}M"
        count >= 100_000 -> String.format(Locale.US, "%.1f lakh", count / 100_000.0)
        count >= 1_000 -> String.format(Locale.US, "%.1fK", count / 1000.0)
        else -> count.toString()
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

fun formatViews(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000.0}M views"
        count >= 100_000 -> "${count / 100_000.0} lakh views"
        count >= 1_000 -> "${count / 1_000}K views"
        else -> "$count views"
    }
}
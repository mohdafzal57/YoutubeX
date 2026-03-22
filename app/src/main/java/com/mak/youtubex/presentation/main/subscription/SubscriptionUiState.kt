package com.mak.youtubex.presentation.main.subscription

import androidx.compose.runtime.Immutable

// 1. Data Model
// @Immutable is a hint to the Compose compiler to skip unnecessary recompositions
// for this class, maximizing performance.
@Immutable
data class SubscriptionItem(
    val id: String,
    val name: String,
    val handle: String,
    val imageUrl: String,
    val hasNewContent: Boolean = false,
    val isNotificationEnabled: Boolean = true
)

// 2. UI State
// Sealed interface forces the UI to handle every possible scenario.
sealed interface SubscriptionUiState {
    data object Loading : SubscriptionUiState

    data class Success(
        val subscriptions: List<SubscriptionItem>,
        val isRefreshing: Boolean = false
    ) : SubscriptionUiState

    data class Error(
        val message: String
    ) : SubscriptionUiState
}
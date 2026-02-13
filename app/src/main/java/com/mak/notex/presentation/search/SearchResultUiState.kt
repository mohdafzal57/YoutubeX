package com.mak.notex.presentation.search

sealed interface SearchResultUiState {
    data object Loading : SearchResultUiState

    data object EmptyQuery : SearchResultUiState

    data object LoadFailed : SearchResultUiState

    data class Success(
        val hasResults: Boolean = true
    ) : SearchResultUiState {
        fun isEmpty() = !hasResults
    }

    data object SearchNotReady : SearchResultUiState
}

package com.mak.youtubex.presentation.main.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mak.youtubex.domain.model.VideoFeed
import com.mak.youtubex.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResultUiState: StateFlow<SearchResultUiState> =
        searchQuery
            .debounce(300L)
            .flatMapLatest { query ->
                when {
                    query.length < SEARCH_QUERY_MIN_LENGTH -> flowOf(SearchResultUiState.EmptyQuery)
                    else -> flowOf(SearchResultUiState.Success())
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SearchResultUiState.Loading
            )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val videos: Flow<PagingData<VideoFeed>> =
        searchQuery
            .debounce(300L)
            .filter { it.length >= SEARCH_QUERY_MIN_LENGTH }
            .flatMapLatest { query ->
                videoRepository.getVideoFeed(
                    query = query
                )
            }
            .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }
}


/** Minimum length where search query is considered as [SearchResultUiState.EmptyQuery] */
private const val SEARCH_QUERY_MIN_LENGTH = 2

/** Minimum number of the fts table's entity count where it's considered as search is not ready */
private const val SEARCH_MIN_FTS_ENTITY_COUNT = 1
private const val SEARCH_QUERY = "searchQuery"

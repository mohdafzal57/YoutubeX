package com.mak.youtubex.presentation.main.channel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mak.youtubex.domain.model.UserChannel
import com.mak.youtubex.domain.model.UserVideo
import com.mak.youtubex.domain.model.UserVideoRequest
import com.mak.youtubex.domain.repository.SubscriptionRepository
import com.mak.youtubex.domain.repository.UserRepository
import com.mak.youtubex.domain.repository.VideoRepository
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.onFailure
import com.mak.youtubex.core.data.util.onSuccess
import com.mak.youtubex.domain.model.Post
import com.mak.youtubex.domain.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val postRepository: SocialRepository,
    private val videoRepository: VideoRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChannelProfileState())
    val uiState: StateFlow<ChannelProfileState> = _uiState.asStateFlow()
    private val sortType = MutableStateFlow(SortType.LATEST)
    
    // Trigger to start collecting posts only when the tab is selected
    private val isPostsTabSelected = MutableStateFlow(false)

    private val _events = Channel<ChannelEvent>()
    val events = _events.receiveAsFlow()

    private val username: String = savedStateHandle["username"] ?: ""

    init {
        loadChannelProfile()
    }

    val videos: Flow<PagingData<UserVideo>> =
        sortType.flatMapLatest { type ->
            videoRepository.getUserVideos(
                UserVideoRequest(
                    username = username,
                    sortBy = "createdAt",
                    sortType = type.value
                )
            )
        }.cachedIn(viewModelScope)

    val userPosts: Flow<PagingData<Post>> =
        isPostsTabSelected.flatMapLatest { selected ->
            if (selected) {
                postRepository.getUserPosts(username)
            } else {
                flowOf(PagingData.empty())
            }
        }.cachedIn(viewModelScope)

    fun onIntent(intent: ChannelIntent) {
        when (intent) {
            ChannelIntent.ToggleSubscription -> toggleSubscription()

            is ChannelIntent.Content -> {
                _uiState.update { it.copy(contentType = intent.contentType) }
                if (intent.contentType == ContentType.POSTS) {
                    isPostsTabSelected.value = true
                }
            }

            is ChannelIntent.OrderType -> {
                _uiState.update { it.copy(sortType = intent.sortType) }
                sortType.update { intent.sortType }
            }
        }
    }

    fun loadChannelProfile() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            userRepository.getUserChannelProfile(username)
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            profile = profile,
                            isLoading = false,
                            isSubscribed = profile.isUserSubscribed
                        )
                    }
                }
                .onFailure { message ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.trySend(ChannelEvent.ShowError(message.toString()))
                }
        }
    }

    fun toggleSubscription() {
        val currentState = _uiState.value
        if (currentState.profile == null) return
        
        _uiState.update { it.copy(isSubscribed = !currentState.isSubscribed) }
        viewModelScope.launch {
            subscriptionRepository.toggleSubscription(currentState.profile.id)
                .onFailure { message ->
                    if (message == NetworkError.EMPTY_HAND) return@onFailure
                    _uiState.update { it.copy(isSubscribed = currentState.isSubscribed) }
                    _events.trySend(ChannelEvent.ShowError(message.toString()))
                }
        }
    }
}

data class ChannelProfileState(
    val isLoading: Boolean = false,
    val profile: UserChannel? = null,
    val isSubscribed: Boolean = false,
    val sortType: SortType = SortType.LATEST,
    val contentType: ContentType = ContentType.VIDEOS,
    val error: String? = null
)

sealed interface ChannelIntent {
    data object ToggleSubscription : ChannelIntent
    data class OrderType(val sortType: SortType) : ChannelIntent
    data class Content(val contentType: ContentType) : ChannelIntent
}

sealed interface ChannelEvent {
    data class ShowError(val message: String) : ChannelEvent
}

enum class SortType(val value: String) {
    LATEST("desc"),
    OLDEST("asc")
}

enum class ContentType {
    VIDEOS,
    POSTS
}

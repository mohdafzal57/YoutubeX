package com.mak.notex.presentation.channel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mak.notex.domain.model.UserChannel
import com.mak.notex.domain.model.UserVideo
import com.mak.notex.domain.model.UserVideoRequest
import com.mak.notex.domain.repository.SubscriptionRepository
import com.mak.notex.domain.repository.UserRepository
import com.mak.notex.domain.repository.VideoRepository
import com.mak.notex.utils.onFailure
import com.mak.notex.utils.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChannelDetailViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val videoRepository: VideoRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChannelProfileState())
    val uiState: StateFlow<ChannelProfileState> = _uiState.asStateFlow()
    private val _effect = Channel<ChannelEffect>()
    val effect = _effect.receiveAsFlow()

    // 1. Keep track of params in a StateFlow
    private val _videoParams = MutableStateFlow(
        UserVideoState(
            userId = savedStateHandle["ownerId"] ?: "",
            sortBy = "createdAt",
            sortType = SortType.LATEST.value // "desc"
        )
    )

    private val username: String = savedStateHandle["username"] ?: ""

    // 2. Use flatMapLatest to react to param changes
    @OptIn(ExperimentalCoroutinesApi::class)
    val videos: Flow<PagingData<UserVideo>> = _videoParams
        .flatMapLatest { params ->
            videoRepository.getUserVideos(
                UserVideoRequest(
                    userId = params.userId,
                    query = params.query,
                    sortBy = params.sortBy,
                    sortType = params.sortType // Ensure your request DTO supports this
                )
            )
        }
        .cachedIn(viewModelScope)

    init {
        loadChannelProfile()
    }

    fun onIntent(intent: ChannelIntent) {
        when (intent) {
            ChannelIntent.ToggleSubscription -> toggleSubscription()
            is ChannelIntent.OrderType -> {
                // Update UI state (for the UI checkmarks)
                _uiState.update { it.copy(sortType = intent.sortType) }

                // Update params (this triggers flatMapLatest and refreshes the list)
                _videoParams.update { it.copy(sortType = intent.sortType.value) }
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
                    _effect.trySend(ChannelEffect.ShowError(message.toString()))
                }
        }
    }

    fun toggleSubscription() {
        val currentState = _uiState.value
        if (currentState.profile == null) {
            return
        }
        _uiState.update { it.copy(isSubscribed = !currentState.isSubscribed) }
        viewModelScope.launch {
            subscriptionRepository.toggleSubscription(currentState.profile.id)
                .onSuccess {
//                    _uiState.update { it.copy(isSubscribed = !currentState.isSubscribed) }
                    _effect.trySend(ChannelEffect.SubscriptionUpdated)
                }
                .onFailure { message ->
                    _effect.trySend(ChannelEffect.ShowError(message.toString()))
                }
        }
    }
}

data class ChannelProfileState(
    val isLoading: Boolean = false,
    val profile: UserChannel? = null,
    val isSubscribed: Boolean = false,
    val sortType: SortType = SortType.LATEST,
    val error: String? = null
)

sealed interface ChannelIntent {
    data object ToggleSubscription : ChannelIntent
    data class OrderType(val sortType: SortType) : ChannelIntent
}

sealed interface ChannelEffect {
    data class ShowError(val message: String) : ChannelEffect
    object SubscriptionUpdated : ChannelEffect
}

data class UserVideoState(
    val userId: String = "",
    val query: String = "",
    val sortBy: String = "createdAt",
    val sortType: String = "desc"
)

enum class SortType(val value: String) {
    LATEST("desc"),
    OLDEST("asc")
}

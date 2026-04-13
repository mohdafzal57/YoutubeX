package com.mak.youtubex.presentation.main.subscription

import android.net.Network
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.onFailure
import com.mak.youtubex.core.data.util.onSuccess
import com.mak.youtubex.core.util.UiText
import com.mak.youtubex.domain.model.SubscriptionProfile
import com.mak.youtubex.domain.repository.SubscriptionRepository
import com.mak.youtubex.utils.asStringRes
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubscriptionUiState>(SubscriptionUiState.Loading)
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SubscriptionEvent>()
    val uiEvent: SharedFlow<SubscriptionEvent> = _uiEvent.asSharedFlow()

    init {
        fetchSubscriptions(isRefresh = false)
    }

    fun refreshSubscriptions() {
        fetchSubscriptions(isRefresh = true)
    }

    private fun fetchSubscriptions(isRefresh: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (isRefresh && currentState is SubscriptionUiState.Success) {
                _uiState.value = currentState.copy(isRefreshing = true)
            } else {
                _uiState.value = SubscriptionUiState.Loading
            }

            repository.getUserSubscribedChannels()
                .onSuccess { profiles ->
                    _uiState.value = SubscriptionUiState.Success(
                        subscriptions = profiles.map { it.toUiModel() },
                        isRefreshing = false
                    )
                }
                .onFailure { error ->
                    val uiText = UiText.StringResource(error.asStringRes())
                    if (isRefresh && _uiState.value is SubscriptionUiState.Success) {
                        _uiState.value = (_uiState.value as SubscriptionUiState.Success).copy(isRefreshing = false)
                        _uiEvent.emit(SubscriptionEvent.Error(uiText))
                    } else {
                        _uiState.value = SubscriptionUiState.Error(uiText)
                    }
                }
        }
    }

    fun unsubscribe(channelId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value as? SubscriptionUiState.Success ?: return@launch
            
            // Optimistic update
            val originalList = currentState.subscriptions
            val updatedList = originalList.filterNot { it.id == channelId }
            _uiState.value = currentState.copy(subscriptions = updatedList)

            repository.toggleSubscription(channelId)
                .onFailure { error ->
                    if (error == NetworkError.EMPTY_HAND) return@launch
                    _uiState.value = currentState.copy(subscriptions = originalList)
                    _uiEvent.emit(SubscriptionEvent.Error(UiText.StringResource(error.asStringRes())))
                }
        }
    }
}

sealed interface SubscriptionEvent {
    data class Error(val message: UiText) : SubscriptionEvent
}

private fun SubscriptionProfile.toUiModel() = SubscriptionItem(
    id = id,
    name = username,
    handle = "@$username",
    imageUrl = avatarUrl,
    hasNewContent = false,
    isNotificationEnabled = true
)

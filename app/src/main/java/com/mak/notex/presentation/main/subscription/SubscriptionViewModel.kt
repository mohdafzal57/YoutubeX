package com.mak.notex.presentation.main.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.notex.domain.model.SubscriptionProfile
import com.mak.notex.domain.repository.SubscriptionRepository
import com.mak.notex.core.data.util.onFailure
import com.mak.notex.core.data.util.onSuccess
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
            val current = _uiState.value

            _uiState.value = if (isRefresh && _uiState.value is SubscriptionUiState.Success) {
                SubscriptionUiState.Success(
                    isRefreshing = true,
                    subscriptions = (current as SubscriptionUiState.Success).subscriptions
                )
            } else {
                SubscriptionUiState.Loading
            }
            repository.getUserSubscribedChannels()
                .onSuccess { profiles ->
                    _uiState.value = SubscriptionUiState.Success(
                        isRefreshing = false,
                        subscriptions = profiles.map { it.toUiModel() }
                    )
                }
                .onFailure {
                    _uiEvent.emit(SubscriptionEvent.Error(it.toString()))
                }
        }
    }

    fun unsubscribe(channelId: String) {
        val currentState = _uiState.value as? SubscriptionUiState.Success ?: return

        val updatedList = currentState.subscriptions.filterNot { it.id == channelId }
        _uiState.value = currentState.copy(subscriptions = updatedList)

        viewModelScope.launch {
            repository.toggleSubscription(channelId)
        }
    }

    private fun SubscriptionProfile.toUiModel() = SubscriptionItem(
        id = id,
        name = username,
        handle = "@$username",
        imageUrl = avatarUrl,
        hasNewContent = false,
        isNotificationEnabled = true
    )
}

sealed interface SubscriptionEvent {
    data class Error(val message: String) : SubscriptionEvent
}
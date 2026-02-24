package com.mak.notex.presentation.main.tweet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.notex.data.remote.api.TweetApi
import com.mak.notex.data.remote.dto.tweet.CreateTweetRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateTweetUiState(
    val content: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)


@HiltViewModel
class CreateTweetViewModel @Inject constructor(
    private val tweetApi: TweetApi
) : ViewModel() {

    var state by mutableStateOf(CreateTweetUiState())
        private set

    fun onContentChange(value: String) {
        state = state.copy(content = value)
    }

    fun createTweet() {
        if (state.content.isBlank()) return

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            tweetApi.createTweet(CreateTweetRequest(state.content))
        }
    }
}

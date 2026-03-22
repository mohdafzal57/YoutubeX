package com.mak.youtubex.presentation.main.social_feed

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Immutable
data class Post(
    val id: String,
    val avatarUrl: String,
    val username: String,
    val handle: String,
    val timestamp: String,
    val body: String,
    val imageUrl: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
)

@Immutable
data class SocialFeedUiState(
    val posts: List<Post> = emptyList(),
    val likedIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class SocialFeedViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        SocialFeedUiState(
            posts = samplePosts
        )
    )
    val uiState: StateFlow<SocialFeedUiState> = _uiState.asStateFlow()

    fun onLikeToggle(postId: String) {
        _uiState.update { state ->
            val updated =
                if (postId in state.likedIds)
                    state.likedIds - postId
                else
                    state.likedIds + postId

            state.copy(likedIds = updated)
        }
    }
}
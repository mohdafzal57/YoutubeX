package com.mak.youtubex.presentation.main.social_feed

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.Result
import com.mak.youtubex.core.data.util.onFailure
import com.mak.youtubex.data.local.PostDao
import com.mak.youtubex.domain.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class Post(
    val id: String,
    val avatarUrl: String,
    val username: String,
    val timestamp: String,
    val body: String,
    val imageUrls: List<String> = emptyList(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false
)

//@Immutable
//data class SocialFeedUiState(
//    val posts: List<Post> = emptyList(),
//    val isLoading: Boolean = false,
//    val error: String? = null
//)

sealed interface SocialFeedAction {
    data class ToggleLike(val postId: String) : SocialFeedAction
}

sealed interface SocialFeedEvent {
    data class ShowError(val message: String) : SocialFeedEvent
}

@HiltViewModel
class SocialFeedViewModel @Inject constructor(
    private val repository: SocialRepository,
    private val postDao: PostDao
) : ViewModel() {

    val posts: Flow<PagingData<Post>> = repository
        .getSocialFeed()
        .cachedIn(viewModelScope)


    private val _events = Channel<SocialFeedEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: SocialFeedAction) {
        when (action) {
            is SocialFeedAction.ToggleLike -> toggleLike(action.postId)
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val post = postDao.getPostById(postId) ?: return@launch
            val currentlyLiked = post.isLiked

            postDao.toggleLike(postId)

            val result = if (currentlyLiked) {
                repository.unLikePost(postId)
            } else {
                repository.likePost(postId)
            }

            result.onFailure {
                if (it == NetworkError.EMPTY_HAND) return@onFailure
                _events.send(SocialFeedEvent.ShowError("Failed to update like"))
                postDao.toggleLike(postId)
            }
        }
    }
}

/*** todo: study it.
 * @[Don't remove it.]
 * private var toggleLikeJob: Job? = null
 *     private fun toggleLike(postId: String) {
 *         val postBeforeChange = _uiState.value.posts.find { it.id == postId } ?: return
 *         val wasLiked = postBeforeChange.isLiked
 *         val originalCount = postBeforeChange.likeCount
 *
 *         val targetLiked = !wasLiked
 *         val targetCount = if (targetLiked) originalCount + 1 else originalCount - 1
 *
 *         updatePostInState(postId, targetCount, targetLiked)
 *
 *         toggleLikeJob?.cancel()
 *         toggleLikeJob = viewModelScope.launch {
 *             try {
 *                 if (wasLiked) {
 *                     repository.unLikePost(postId)
 *                 } else {
 *                     repository.likePost(postId)
 *                 }
 *             } catch(e: Exception) {
 *                 updatePostInState(postId, originalCount, wasLiked)
 *                 _events.send(SocialFeedEvent.ShowError("Failed to update like"))
 *             }
 *         }
 *     }
 *
 *     private fun updatePostInState(postId: String, count: Int, isLiked: Boolean) {
 *         _uiState.update { state ->
 *             state.copy(
 *                 posts = state.posts.map { post ->
 *                     if (post.id == postId) {
 *                         post.copy(isLiked = isLiked, likeCount = count)
 *                     } else {
 *                         post
 *                     }
 *                 }
 *             )
 *         }
 *     }*/


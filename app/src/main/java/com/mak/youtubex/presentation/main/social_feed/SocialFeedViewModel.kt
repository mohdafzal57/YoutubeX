package com.mak.youtubex.presentation.main.social_feed

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.onFailure
import com.mak.youtubex.core.data.util.onSuccess
import com.mak.youtubex.data.local.PostDao
import com.mak.youtubex.domain.model.Comment
import com.mak.youtubex.domain.model.Post
import com.mak.youtubex.domain.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SocialFeedAction {
    data class ToggleLike(val postId: String) : SocialFeedAction
    data class AddComment(val postId: String, val content: String) : SocialFeedAction
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


    private val _events = MutableSharedFlow<SocialFeedEvent>()
    val events = _events.asSharedFlow()

    fun onAction(action: SocialFeedAction) {
        when (action) {
            is SocialFeedAction.ToggleLike -> toggleLike(action.postId)
            is SocialFeedAction.AddComment -> addComment(action.postId, action.content)
        }
    }

    private val likeJob: MutableMap<String, Job?> = mutableMapOf()

    fun toggleLike(postId: String) {
        likeJob[postId]?.cancel()

        likeJob[postId] = viewModelScope.launch {
            val post = postDao.getPostById(postId) ?: return@launch
            val originalState = post.isLiked

            postDao.toggleLike(postId) // optimistic update

            delay(500) // debounce - wait for rapid-taps

            val finalPost = postDao.getPostById(postId) ?: return@launch
            val finalState = finalPost.isLiked

            if (originalState == finalState) return@launch

            // Re-read final intended state after debounce
            val result = if (finalState) {
                repository.likePost(postId)
            } else {
                repository.unLikePost(postId)
            }

            result.onFailure {
                if (it == NetworkError.EMPTY_HAND) return@onFailure
                _events.emit(SocialFeedEvent.ShowError("Failed to update like"))
                postDao.toggleLike(postId)
            }
        }
    }

    fun getComments(postId: String): Flow<PagingData<Comment>> {
        return repository.getComments(postId).cachedIn(viewModelScope)
    }

    private val _isSendingComment = MutableStateFlow(false)
    val isSendingComment: StateFlow<Boolean> = _isSendingComment

    private fun addComment(postId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _isSendingComment.value = true
            repository.addComment(postId, content)
                .onSuccess {
                    postDao.incrementCommentCount(postId)
                    _isSendingComment.value = false
                }
                .onFailure {
                    _isSendingComment.value = false
                    _events.emit(SocialFeedEvent.ShowError("Failed to add comment"))
                }
        }
    }
}

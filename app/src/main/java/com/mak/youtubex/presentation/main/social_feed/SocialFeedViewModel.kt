package com.mak.youtubex.presentation.main.social_feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mak.youtubex.core.data.util.NetworkError
import com.mak.youtubex.core.data.util.onFailure
import com.mak.youtubex.core.data.util.onSuccess
import com.mak.youtubex.core.datastore.TokenManager
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

sealed interface SocialFeedAction {
    data class ToggleLike(val postId: String) : SocialFeedAction
    data class AddComment(val postId: String, val content: String) : SocialFeedAction
}

sealed interface SocialFeedEvent {
    data class CommentAdded(val postId: String) : SocialFeedEvent
}

@HiltViewModel
class SocialFeedViewModel @Inject constructor(
    private val repository: SocialRepository,
    private val postDao: PostDao,
    private val tokenManager: TokenManager
) : ViewModel() {

    val posts: Flow<PagingData<Post>> = repository
        .getSocialFeed()
        .cachedIn(viewModelScope)

    val avatar: StateFlow<String?> = tokenManager.session
        .map { it.avatar }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    private val _events = MutableSharedFlow<SocialFeedEvent>()
    val events = _events.asSharedFlow()

    private val _isSendingComment = MutableStateFlow(false)
    val isSendingComment: StateFlow<Boolean> = _isSendingComment.asStateFlow()

    // Thread-safe: accessed from multiple coroutines
    private val likeJobs = ConcurrentHashMap<String, Job>()

    // Cache comment flows per postId to avoid recreating pagers
    private val commentFlowCache = ConcurrentHashMap<String, Flow<PagingData<Comment>>>()

    fun onAction(action: SocialFeedAction) {
        when (action) {
            is SocialFeedAction.ToggleLike -> toggleLike(action.postId)
            is SocialFeedAction.AddComment -> addComment(action.postId, action.content)
        }
    }

    fun getComments(postId: String): Flow<PagingData<Comment>> {
        return commentFlowCache.getOrPut(postId) {
            repository.getComments(postId).cachedIn(viewModelScope)
        }
    }

    private fun toggleLike(postId: String) {
        likeJobs[postId]?.cancel()

        likeJobs[postId] = viewModelScope.launch {
            val post = postDao.getPostById(postId) ?: return@launch
            val originalState = post.isLiked

            postDao.toggleLike(postId) // optimistic update

            delay(500) // debounce rapid taps

            val finalPost = postDao.getPostById(postId) ?: return@launch
            val finalState = finalPost.isLiked

            // No net change after debounce — skip network call
            if (originalState == finalState) return@launch

            val result = if (finalState) repository.likePost(postId)
            else repository.unLikePost(postId)

            result.onFailure { error ->
                if (error == NetworkError.EMPTY_HAND) return@onFailure
                postDao.toggleLike(postId) // rollback
            }
        }.also { job ->
            // Clean up map entry when job completes to avoid unbounded growth
            job.invokeOnCompletion { likeJobs.remove(postId) }
        }
    }

    private fun addComment(postId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _isSendingComment.value = true
            try {
                repository.addComment(postId, content)
                    .onSuccess {
                        postDao.incrementCommentCount(postId)
                        _events.emit(SocialFeedEvent.CommentAdded(postId))
                    }
            } finally {
                _isSendingComment.value = false
            }
        }
    }
}
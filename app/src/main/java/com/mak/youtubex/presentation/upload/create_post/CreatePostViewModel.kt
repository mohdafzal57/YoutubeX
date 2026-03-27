package com.mak.youtubex.presentation.upload.create_post

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.youtubex.core.data.util.onFailure
import com.mak.youtubex.core.data.util.onSuccess
import com.mak.youtubex.domain.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.fold

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val repository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CreatePostEvent>()
    val event: SharedFlow<CreatePostEvent> = _event.asSharedFlow()

    fun onAction(action: CreatePostAction) {
        when (action) {

            is CreatePostAction.OnTextChange -> {
                _uiState.update { it.copy(text = action.text) }
            }

            is CreatePostAction.OnVisibilityChange -> {
                _uiState.update { it.copy(visibility = action.visibility) }
            }

            is CreatePostAction.OnMediaSelected -> {
                _uiState.update { it.copy(media = action.uris) }
            }

            is CreatePostAction.OnRemoveMedia -> {
                _uiState.update {
                    it.copy(media = it.media - action.uri)
                }
            }

            CreatePostAction.OnSubmit -> {
                submitPost()
            }
        }
    }


    fun submitPost() {
        val current = _uiState.value

        if (current.text.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.createPost(
                content = current.text,
                visibility = current.visibility.name.lowercase(),
                images = current.media
            )

            result.onSuccess  {
                    _event.emit(CreatePostEvent.Success)
                    _uiState.value = PostUiState()
                }
                .onFailure  { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.toString()
                        )
                    }
                    _event.emit(CreatePostEvent.Error(error.toString()))
                }

        }
    }
}

/* -------------------- STATE -------------------- */

data class PostUiState(
    val text: String = "",
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val media: List<Uri> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/* -------------------- EVENTS -------------------- */

sealed class CreatePostEvent {
    data object Success : CreatePostEvent()
    data class Error(val message: String) : CreatePostEvent()
}

/* -------------------- ENUM -------------------- */

enum class PostVisibility {
    PUBLIC, PRIVATE
}

sealed interface CreatePostAction {

    data class OnTextChange(val text: String) : CreatePostAction

    data class OnVisibilityChange(val visibility: PostVisibility) : CreatePostAction

    data class OnMediaSelected(val uris: List<Uri>) : CreatePostAction

    data class OnRemoveMedia(val uri: Uri) : CreatePostAction

    data object OnSubmit : CreatePostAction
}
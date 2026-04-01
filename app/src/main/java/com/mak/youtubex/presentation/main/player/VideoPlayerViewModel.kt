package com.mak.youtubex.presentation.main.player

import androidx.lifecycle.ViewModel
import com.mak.youtubex.presentation.upload_video.VideoPlayerAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor() : ViewModel() {

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _showControls = MutableStateFlow(false)
    val showControls: StateFlow<Boolean> = _showControls.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration: StateFlow<Long> = _totalDuration.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    fun onAction(action: VideoPlayerAction) {
        when (action) {

            is VideoPlayerAction.ToggleControls -> {
                _showControls.value = !_showControls.value
            }

            is VideoPlayerAction.HideControls -> {
                _showControls.value = false
            }

            is VideoPlayerAction.PlayPause -> {
                _isPlaying.value = action.play
            }

            is VideoPlayerAction.Seek -> {
                _currentPosition.value = action.position
            }

            is VideoPlayerAction.Progress -> {
                _currentPosition.value = action.position
                _totalDuration.value = action.duration
            }

            is VideoPlayerAction.Mute -> {
                _isMuted.value = action.mute
            }
        }
    }

    // Keep this only if syncing with ExoPlayer callbacks
    fun syncPlayerState(isPlayingNow: Boolean) {
        _isPlaying.value = isPlayingNow
    }
}
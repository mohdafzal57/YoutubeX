package com.mak.youtubex.presentation.main.player

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mak.youtubex.core.device.DeviceIdProvider
import com.mak.youtubex.domain.repository.VideoRepository
import com.mak.youtubex.presentation.upload_video.VideoPlayerAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val savedStateHandle: SavedStateHandle,
    private val deviceIdProvider: DeviceIdProvider
) : ViewModel() {

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

    private val hasSend = mutableStateOf(false)
    private val watchTime = MutableStateFlow(0L)
    val videoId = savedStateHandle.get<String>("videoId") ?: ""
    val deviceId = deviceIdProvider.getDeviceId()

    init {
        watchTimeCounter()
        observeCurrentWatchTime()
    }

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

    private fun observeCurrentWatchTime() {
        viewModelScope.launch {
            watchTime.collectLatest { currentWatchTime ->
                if (!hasSend.value && currentWatchTime >= 5000L) {
                    hasSend.value = true
                    videoRepository.addView(videoId, deviceId)
                }
            }
        }
    }

    private fun watchTimeCounter() {
        viewModelScope.launch {
            _isPlaying.collectLatest { isPlaying ->
                if (isPlaying) {
                    var last = System.currentTimeMillis()
                    while (true) {
                        val now = System.currentTimeMillis()
                        watchTime.value += (now - last)
                        last = now
                        delay(1000L)
                    }
                }
            }
        }
    }
}
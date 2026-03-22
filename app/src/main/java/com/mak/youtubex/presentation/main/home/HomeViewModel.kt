package com.mak.youtubex.presentation.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mak.youtubex.domain.model.VideoFeed
import com.mak.youtubex.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
) : ViewModel() {

    val videos: Flow<PagingData<VideoFeed>> =
        videoRepository.getVideoFeed()
            .cachedIn(viewModelScope)

}
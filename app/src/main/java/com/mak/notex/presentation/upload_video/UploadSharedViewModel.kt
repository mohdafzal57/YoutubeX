package com.mak.notex.presentation.upload_video

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UploadSharedViewModel @Inject constructor() : ViewModel() {

    private val _videoUri = MutableStateFlow<Uri?>(null)
    val videoUri: StateFlow<Uri?> = _videoUri

    fun setVideo(uri: Uri) {
        _videoUri.value = uri
    }

    fun clear() {
        _videoUri.value = null
    }
}
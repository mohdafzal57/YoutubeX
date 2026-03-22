package com.mak.youtubex.presentation.main.profile

import android.net.Uri
import com.mak.youtubex.domain.model.User

// State
data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// Intent
sealed class ProfileIntent {
    object LoadProfile : ProfileIntent()
    data class UpdateAvatar(val uri: Uri) : ProfileIntent()
    data class UpdateCoverImage(val uri: Uri) : ProfileIntent()
    object SignOut : ProfileIntent()
}
sealed class ProfileEffect {
    object NavigateToSignIn : ProfileEffect()
    data class ShowError(val message: String) : ProfileEffect()
    data class ShowSuccess(val message: String) : ProfileEffect()
}
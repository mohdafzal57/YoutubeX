package com.mak.youtubex.utils

import androidx.annotation.StringRes
import com.mak.youtubex.R
import com.mak.youtubex.core.data.util.NetworkError

@StringRes
fun NetworkError.asStringRes(): Int {
    return when (this) {
        NetworkError.NO_INTERNET -> R.string.error_no_internet
        NetworkError.TIMEOUT -> R.string.error_timeout
        NetworkError.UNAUTHORIZED -> R.string.error_unauthorized
        NetworkError.TOO_MANY_REQUESTS -> R.string.error_too_many_requests
        NetworkError.SERVER -> R.string.error_server
        NetworkError.SERIALIZATION -> R.string.error_serialization
        NetworkError.EMPTY_BODY -> R.string.error_empty_body
        NetworkError.EMPTY_HAND -> R.string.error_empty_hand
        NetworkError.UNKNOWN -> R.string.error_unknown
        NetworkError.FORBIDDEN -> R.string.error_forbidden
        NetworkError.NOT_FOUND -> R.string.error_not_found
        NetworkError.CLIENT -> R.string.error_client
    }
}


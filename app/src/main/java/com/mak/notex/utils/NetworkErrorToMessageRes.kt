package com.mak.notex.utils

import androidx.annotation.StringRes
import com.mak.notex.R

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
        NetworkError.UNKNOWN -> R.string.error_unknown
    }
}


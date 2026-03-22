package com.mak.youtubex.utils

import com.mak.youtubex.core.data.util.NetworkError

fun mapHttpError(code: Int): NetworkError =
    when (code) {
        401 -> NetworkError.UNAUTHORIZED
        403 -> NetworkError.FORBIDDEN
        404 -> NetworkError.NOT_FOUND
        408 -> NetworkError.TIMEOUT
        429 -> NetworkError.TOO_MANY_REQUESTS
        in 400..499 -> NetworkError.CLIENT
        in 500..599 -> NetworkError.SERVER
        else -> NetworkError.UNKNOWN
    }

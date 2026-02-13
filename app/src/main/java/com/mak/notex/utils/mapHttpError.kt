package com.mak.notex.utils

fun mapHttpError(code: Int): NetworkError =
    when (code) {
        401 -> NetworkError.UNAUTHORIZED
        408 -> NetworkError.TIMEOUT
        429 -> NetworkError.TOO_MANY_REQUESTS
        in 500..599 -> NetworkError.SERVER
        else -> NetworkError.UNKNOWN
    }

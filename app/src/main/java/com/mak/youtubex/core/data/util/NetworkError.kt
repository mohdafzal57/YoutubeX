package com.mak.youtubex.core.data.util

enum class NetworkError : Error {
    NO_INTERNET,
    TIMEOUT,
    UNAUTHORIZED,
    TOO_MANY_REQUESTS,
    SERVER,
    SERIALIZATION,
    EMPTY_BODY,
    EMPTY_HAND,
    UNKNOWN,
    FORBIDDEN,
    NOT_FOUND,
    CLIENT
}

package com.mak.notex.utils

enum class NetworkError : Error {
    NO_INTERNET,
    TIMEOUT,
    UNAUTHORIZED,
    TOO_MANY_REQUESTS,
    SERVER,
    SERIALIZATION,
    EMPTY_BODY,
    UNKNOWN
}

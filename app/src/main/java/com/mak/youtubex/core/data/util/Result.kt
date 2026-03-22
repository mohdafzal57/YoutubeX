package com.mak.youtubex.core.data.util

typealias DomainError = Error

sealed interface Result<out T, out E: DomainError> {
    data class Success<out T>(val data: T): Result<T, Nothing>
    data class Error<out E: DomainError>(val error: E): Result<Nothing, E>
}

inline fun <T, E: Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Success -> {
            action(data)
            this
        }
        is Result.Error -> this
    }
}
inline fun <T, E: Error> Result<T, E>.onFailure(action: (E) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> {
            action(error)
            this
        }
        is Result.Success -> this
    }
}
inline fun <T, E: Error, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
    }
}
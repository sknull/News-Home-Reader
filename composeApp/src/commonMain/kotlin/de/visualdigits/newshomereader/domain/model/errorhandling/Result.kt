package de.visualdigits.newshomereader.domain.model.errorhandling

import androidx.compose.runtime.Immutable

sealed interface Result<out D, out E: AppError> {
    @Immutable
    data class Success<out D>(val data: D): Result<D, Nothing>
    @Immutable
    data class Error<out E: AppError>(val error: E, val throwable: Throwable? = null): Result<Nothing, E>
}

fun <T, E: AppError, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

fun <T, E: AppError> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  }
}

fun <T, E: AppError> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T, E: AppError> Result<T, E>.onError(action: (E, Throwable?) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> {
            action(error, throwable)
            this
        }
        is Result.Success -> this
    }
}

typealias EmptyResult<E> = Result<Unit, E>

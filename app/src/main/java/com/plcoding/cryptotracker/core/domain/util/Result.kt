package com.plcoding.cryptotracker.core.domain.util


// https://www.youtube.com/watch?v=MiLN2vs2Oe0


/**
 * Type alias for representing domain-specific errors.
 */
typealias DomainError = Error

/**
 * A sealed interface representing a result of an operation, which can either be a success or an error.
 *
 * @param D The type of the successful result.
 * @param E The type of the error, which must extend [Error].
 */
sealed interface Result<out D, out E : Error> {
    /**
     * Represents a successful result containing data.
     *
     * @param D The type of the successful result data.
     * @property data The successful result data.
     */
    data class Success<out D>(val data: D) : Result<D, Nothing>

    /**
     * Represents an error result containing an error of type [E].
     *
     * @param E The type of the error, which must extend [DomainError].
     * @property error The error object.
     */
    data class Error<out E : DomainError>(val error: E) : Result<Nothing, E>
}

/**
 * Transforms the successful result's data using the given mapping function.
 *
 * @param map A function to transform the success data.
 * @return A new [Result] instance with transformed data, or the same error result if the original result was an error.
 */
inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error) // Preserve the error result as-is.
        is Result.Success -> Result.Success(map(data)) // Apply the mapping function to the data.
    }
}

/**
 * Converts a [Result] into an [EmptyResult], discarding its success data.
 *
 * @return A new [EmptyResult] instance, preserving the error if present.
 */
fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { } // Maps the success data to Unit (empty result)
}

/**
 * Executes the given action if the result is a success.
 *
 * @param action A function to execute when the result is successful.
 * @return The original result, allowing method chaining.
 */
inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> this // Return error result unchanged.
        is Result.Success -> {
            action(data) // Execute the provided action.
            this // Return the original success result.
        }
    }
}

/**
 * Executes the given action if the result is an error.
 *
 * @param action A function to execute when the result is an error.
 * @return The original result, allowing method chaining.
 */
inline fun <T, E : Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> {
            action(error) // Execute the provided action.
            this // Return the original error result.
        }

        is Result.Success -> this // Return success result unchanged.
    }
}

/**
 * Type alias for a [Result] with an empty success type.
 */
typealias EmptyResult<E> = Result<Unit, E>

package com.plcoding.cryptotracker.core.data.networking

import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.core.domain.util.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

/**
 * Converts an [HttpResponse] to a [Result] object containing either a successful response body or an error.
 *
 * @param response The HTTP response to be processed.
 * @return A [Result] containing the parsed response body of type [T] on success,
 *         or a [NetworkError] on failure.
 */
suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, NetworkError> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                // Attempt to parse the response body into the expected type
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                // Handle serialization issues
                Result.Error(NetworkError.SERIALIZATION)
            }
        }

        // Handle specific HTTP error codes
        408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
        429 -> Result.Error(NetworkError.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)

        // Handle any other unknown errors
        else -> Result.Error(NetworkError.UNKNOWN)
    }
}

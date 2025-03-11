package com.plcoding.cryptotracker.core.data.networking

import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.core.domain.util.Result
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import kotlin.coroutines.coroutineContext

/**
 * Executes a network call safely, handling common exceptions and converting responses into a [Result].
 *
 * @param execute A lambda function that executes the network request and returns an [HttpResponse].
 * @return A [Result] containing the successful response body of type [T] or a [NetworkError] on failure.
 */
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, NetworkError> {
    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        // Handle no internet connection errors
        return Result.Error(NetworkError.NO_INTERNET)
    } catch (e: SerializationException) {
        // Handle JSON serialization errors
        return Result.Error(NetworkError.SERIALIZATION)
    } catch (e: Exception) {
        // Ensure coroutine is still active before returning error
        // This prevents catching CoroutineCancellationException, allowing it to propagate properly
        // https://youtu.be/VWlwkqmTLHc?si=KGa_GMuFrJx-ghQI&t=1065
        coroutineContext.ensureActive()
        return Result.Error(NetworkError.UNKNOWN)
    }

    // Convert the HttpResponse to a Result object
    return responseToResult(response)
}

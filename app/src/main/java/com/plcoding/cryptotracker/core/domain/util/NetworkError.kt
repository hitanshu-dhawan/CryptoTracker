package com.plcoding.cryptotracker.core.domain.util

enum class NetworkError : Error {
    NO_INTERNET,        // No network connection
    REQUEST_TIMEOUT,    // 408 Request Timeout
    TOO_MANY_REQUESTS,  // 429 Too Many Requests
    SERVER_ERROR,       // 500+ Server errors
    SERIALIZATION,      // Malformed response / serialization issue
    UNKNOWN,            // Any other unknown error
}

/**
 * Returns a human-readable message for the network error.
 */
fun NetworkError.getMessage(): String {
    return when (this) {
        NetworkError.NO_INTERNET -> "No internet connection"
        NetworkError.REQUEST_TIMEOUT -> "Request timed out"
        NetworkError.TOO_MANY_REQUESTS -> "Too many requests"
        NetworkError.SERVER_ERROR -> "Server error"
        NetworkError.SERIALIZATION -> "Failed to parse response"
        NetworkError.UNKNOWN -> "An unknown error occurred"
    }
}

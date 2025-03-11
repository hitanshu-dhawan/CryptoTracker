package com.plcoding.cryptotracker.core.domain.util

enum class NetworkError : Error {
    NO_INTERNET,        // No network connection
    REQUEST_TIMEOUT,    // 408 Request Timeout
    TOO_MANY_REQUESTS,  // 429 Too Many Requests
    SERVER_ERROR,       // 500+ Server errors
    SERIALIZATION,      // Malformed response / serialization issue
    UNKNOWN,            // Any other unknown error
}

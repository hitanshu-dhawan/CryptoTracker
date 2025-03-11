package com.plcoding.cryptotracker.core.data.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory object for creating instances of [HttpClient].
 * This client is configured with logging, content negotiation, and default request settings.
 */
object HttpClientFactory {

    /**
     * Creates and configures an [HttpClient] using the provided [engine].
     *
     * @param engine The [HttpClientEngine] to be used by the client.
     * @return A configured instance of [HttpClient].
     */
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            // Install Logging plugin to log network requests and responses
            install(Logging) {
                level = LogLevel.ALL // Logs all network details
                logger = Logger.ANDROID // Uses Android logger for logging
            }

            // Install ContentNegotiation to handle JSON serialization and deserialization
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true // Ignores unknown fields in JSON response
                    }
                )
            }

            // Set default request settings for all HTTP calls
            defaultRequest {
                contentType(ContentType.Application.Json) // Sets default content type to JSON
            }
        }
    }
}

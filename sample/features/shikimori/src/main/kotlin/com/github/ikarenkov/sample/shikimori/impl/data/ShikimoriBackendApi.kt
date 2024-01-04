package com.github.ikarenkov.sample.shikimori.impl.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import logcat.logcat

class ShikimoriBackendApi {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        prettyPrint = true
    }

    private val client = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    logcat(tag = "ShikimoriApi") { message }
                    Log.d("ShikimoriApi", message)
                }
            }
            level = LogLevel.ALL
        }
    }

    private val baseApiPath = "shikimori.one/api"

    suspend fun animes(page: Int = 1, limit: Int = 10): Result<List<AnimeResponse>> =
        client.runCatching {
            get {
                path("animes")
                url {
                    parameter("page", page)
                    parameter("limit", limit)
                }
            }.body()
        }

    private fun HttpRequestBuilder.path(endpoint: String) {
        url {
            protocol = URLProtocol.HTTPS
            host = baseApiPath
            path(endpoint)
        }
    }

}
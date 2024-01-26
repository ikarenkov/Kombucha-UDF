package com.github.ikarenkov.sample.shikimori.impl.data

import com.github.ikarenkov.sample.shikimori.api.shikimoriFeatureFacade
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.http.userAgent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import logcat.logcat

internal class HttpClientFactory {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        prettyPrint = true
    }

    fun createClient(): HttpClient = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    logcat(tag = "ShikimoriBackendApi") { message }
                }
            }
            level = LogLevel.ALL
        }
        install(Auth) {
            setupAuth()
        }
        defaultRequest {
            userAgent("Kombucha TEA")
            url {
                protocol = URLProtocol.HTTPS
                host = "shikimori.one"
            }
        }
    }

    private fun Auth.setupAuth() {
        bearer {
            loadTokens {
                val authFeature = shikimoriFeatureFacade.scope.get<AuthFeature>()
                val authData = authFeature.state
                    .filter { it !is AuthFeature.State.Init }
                    .first() as? AuthFeature.State.Authorized
                authData?.let {
                    BearerTokens(accessToken = it.accessToken, refreshToken = it.refreshToken)
                }
            }
            refreshTokens {
                val authStore = shikimoriFeatureFacade.scope.get<AuthFeature>()
                authStore.accept(AuthFeature.Msg.RefreshToken)
                val newAuthData = authStore.state
                    // What if error happened? How to catch it here?
                    .first { state ->
                        when (state) {
                            is AuthFeature.State.Authorized -> {
                                state.accessToken != oldTokens?.accessToken || state.failedRefreshAccessToken
                            }
                            is AuthFeature.State.NotAuthorized, is AuthFeature.State.Init -> false
                        }
                    } as AuthFeature.State.Authorized
                newAuthData.takeIf { !it.failedRefreshAccessToken }?.let {
                    BearerTokens(it.accessToken, it.refreshToken)
                }
            }
            sendWithoutRequest {
                it.url.toString() != "https://shikimori.one/oauth/token"
            }
        }
    }
}
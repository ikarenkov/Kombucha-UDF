package com.github.ikarenkov.sample.shikimori.impl.data

import com.github.ikarenkov.sample.shikimori.impl.auth.data.AccessTokenRequest
import com.github.ikarenkov.sample.shikimori.impl.auth.data.AccessTokenResponse
import com.github.ikarenkov.sample.shikimori.impl.auth.data.RefreshTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.path

internal class ShikimoriBackendApi(
    private val client: HttpClient
) {

    fun invalidateBearerTokens() {
        try {
            client
                .plugin(Auth)
                .providers
                .filterIsInstance<BearerAuthProvider>()
                .first()
                .clearToken()
        } catch (ignored: IllegalStateException) {
            // No-op; plugin not installed
        }
    }

    suspend fun animes(page: Int = 1, limit: Int = 10): Result<List<AnimeResponse>> =
        client.runCatching {
            get {
                url {
                    path("api/animes")
                    parameter("page", page)
                    parameter("limit", limit)
                }
            }.body()
        }

    suspend fun refreshTokens(refreshToken: String): Result<AccessTokenResponse> =
        client.runCatching {
            post {
                url.path("oauth/token")

                contentType(ContentType.Application.Json)
                setBody(
                    RefreshTokenRequest(
                        grantType = "refresh_token",
                        clientId = ShikimoriCredentials.CLIENT_ID,
                        clientSecret = ShikimoriCredentials.CLIENT_SECRET,
                        refreshToken = refreshToken
                    )
                )
            }.body()
        }

    suspend fun getAccessToken(oauthCode: String): Result<AccessTokenResponse> =
        client.runCatching {
            post {
                url.path("oauth/token")

                contentType(ContentType.Application.Json)
                headers {

                }
                setBody(
                    AccessTokenRequest(
                        grantType = "authorization_code",
                        clientId = ShikimoriCredentials.CLIENT_ID,
                        clientSecret = ShikimoriCredentials.CLIENT_SECRET,
                        code = oauthCode
                    )
                )
            }.body()
        }

}
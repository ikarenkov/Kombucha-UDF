package com.github.ikarenkov.sample.shikimori.impl.auth.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RefreshTokenRequest(
    @SerialName("grant_type")
    val grantType: String,
    @SerialName("client_id")
    val clientId: String,
    @SerialName("client_secret")
    val clientSecret: String,
    @SerialName("refresh_token")
    val refreshToken: String,
)
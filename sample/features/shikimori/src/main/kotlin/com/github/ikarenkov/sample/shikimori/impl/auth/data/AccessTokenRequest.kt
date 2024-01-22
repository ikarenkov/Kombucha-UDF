package com.github.ikarenkov.sample.shikimori.impl.auth.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AccessTokenRequest(
    @SerialName("grant_type")
    val grantType: String,
    @SerialName("client_id")
    val clientId: String,
    @SerialName("client_secret")
    val clientSecret: String,
    @SerialName("code")
    val code: String,
    @SerialName("redirect_uri")
    val redirectUri: String = "kombucha.shikimori://oauth",
)
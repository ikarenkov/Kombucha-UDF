package com.github.ikarenkov.sample.shikimori.impl.data

import kotlinx.serialization.Serializable

@Serializable
data class AnimesRequest(
    val page: Int? = null,
    val limit: Int? = null,
)
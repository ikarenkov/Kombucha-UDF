package com.github.ikarenkov.sample.shikimori.impl.data

import kotlinx.serialization.Serializable

@Serializable
data class AnimeResponse(
    val id: Int,
    val name: String
)
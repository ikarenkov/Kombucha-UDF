package com.github.ikarenkov.sample.shikimori.impl.data

import kotlinx.serialization.Serializable

@Serializable
internal data class AnimeResponse(
    val id: Int,
    val name: String,
    val image: Image? = null
) {

    @Serializable
    data class Image(
        val original: String? = null,
        val preview: String? = null
    )

}
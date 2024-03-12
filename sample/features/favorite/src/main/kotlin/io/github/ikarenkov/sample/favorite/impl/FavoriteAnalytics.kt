package io.github.ikarenkov.sample.favorite.impl

import logcat.logcat

internal class FavoriteAnalytics {

    fun itemClick(id: String, isFavorite: Boolean) {
        logcat(tag = "FavoriteAnalytics") {
            "item clicked: id = $id, isFavorite = $isFavorite"
        }
    }

    fun changeFavoriteClick(id: String, desiredFavorite: Boolean) {
        logcat(tag = "FavoriteAnalytics") {
            "change favorite click: id = $id, desiredFavorite = $desiredFavorite"
        }
    }

}
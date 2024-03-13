package io.github.ikarenkov.sample.favorite.impl.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface FavoriteListItem : Parcelable {

    @Parcelize
    data class Item(
        val id: String,
        val title: String
    ) : FavoriteListItem

    @Parcelize
    data class Skeleton(val pos: Int) : FavoriteListItem

}
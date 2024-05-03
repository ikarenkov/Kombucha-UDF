package io.github.ikarenkov.sample.favorite.impl.data

import java.io.Serializable

data class FavoriteItem(
    val id: String,
    val title: String = "Item #$id",
    val isFavorite: Boolean = true,
    /**
     * When it is true, [isFavorite] shows what it should be, but not what it is on remote.
     * When we have an error updating favorite, then we must set [isFavorite] to the previous one - opposite.
     */
    val updatingFavorite: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = -3343265517455468090L
    }
}

/**
 * Copies and sets [FavoriteItem.isFavorite] and [FavoriteItem.updatingFavorite] to opposite.
 */
fun FavoriteItem.changeFavorite() = copy(isFavorite = !isFavorite, updatingFavorite = !updatingFavorite)

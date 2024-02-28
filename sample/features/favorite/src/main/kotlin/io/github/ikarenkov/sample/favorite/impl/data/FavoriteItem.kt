package io.github.ikarenkov.sample.favorite.impl.data

data class FavoriteItem(
    val id: String,
    val title: String,
    val isFavorite: Boolean = true,
    /**
     * When it is true, [isFavorite] shows what it should be, but not what it is on remote.
     * When we have an error updating favorite, then we must set [isFavorite] to the previous one - opposite.
     */
    val updatingFavorite: Boolean = false
)

/**
 * Copies and sets [FavoriteItem.isFavorite] and [FavoriteItem.updatingFavorite] to opposite.
 */
fun FavoriteItem.changeFavorite() = copy(isFavorite = !isFavorite, updatingFavorite = !updatingFavorite)

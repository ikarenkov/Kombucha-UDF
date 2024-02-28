package io.github.ikarenkov.sample.favorite.impl.ui

import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature
import io.github.ikarenkov.sample.favorite.impl.core.LCE
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteItem

internal object DemoFavUiConverter {

    private const val SHIMMERS_COUNT = 5

    fun convert(state: FavoriteFeature.State): DemoFavUiState = when (state.content) {
        is LCE.Initial, is LCE.Loading -> DemoFavUiState(LCE.Data(getShimmerCells()))
        is LCE.Data -> DemoFavUiState(LCE.Data(toUiItem(state.content.value)))
        is LCE.Error -> DemoFavUiState(LCE.Error(state.content.error))
    }

    private fun toUiItem(items: List<FavoriteItem>): List<FavoriteListItem> =
        items.mapNotNull { item ->
            item
                .takeIf { it.isFavorite }
                ?.let { FavoriteListItem.Item(item.id, item.title) }
        }

    private fun getShimmerCells(): List<FavoriteListItem.Skeleton> = List(SHIMMERS_COUNT) { FavoriteListItem.Skeleton(it) }

}

package io.github.ikarenkov.sample.favorite.impl.ui

import io.github.ikarenkov.sample.core.pagination.PaginationState
import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteAggregatedFeature
import io.github.ikarenkov.sample.favorite.impl.core.LCE
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteItem

internal object FavoriteUiConverter {

    private const val SHIMMERS_COUNT = 5

    fun convert(state: FavoriteFeature.State): FavoriteUiState = when (state.content) {
        is LCE.Initial, is LCE.Loading -> FavoriteUiState(LCE.Data(getShimmerCells()))
        is LCE.Data -> FavoriteUiState(LCE.Data(toUiItem(state.content.value)))
        is LCE.Error -> FavoriteUiState(LCE.Error(state.content.error))
    }

    private fun toUiItem(items: List<FavoriteItem>): List<FavoriteListItem> =
        items.mapNotNull { item ->
            item
                .takeIf { it.isFavorite }
                ?.let { FavoriteListItem.Item(item.id, item.title) }
        }

    private fun getShimmerCells(): List<FavoriteListItem.Skeleton> = List(SHIMMERS_COUNT) { FavoriteListItem.Skeleton(it) }

    fun convert(state: FavoriteAggregatedFeature.State): FavoriteUiState {
        val paginationState = state.pagination
        return when {
            paginationState.items.isEmpty() -> {
                when(val pageLoading = paginationState.nextPageLoadingState) {
                    PaginationState.PageLoadingState.Loading, PaginationState.PageLoadingState.Idle -> FavoriteUiState(LCE.Data(getShimmerCells()))
                    is PaginationState.PageLoadingState.Error ->  FavoriteUiState(LCE.Error(pageLoading.throwable))
                }
            }
            else -> {
                FavoriteUiState(LCE.Data(toUiItem(paginationState.items)))
            }
        }
    }

}

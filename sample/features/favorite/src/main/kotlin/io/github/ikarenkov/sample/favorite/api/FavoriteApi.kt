package io.github.ikarenkov.sample.favorite.api

import com.github.terrakok.modo.Screen
import io.github.ikarenkov.sample.favorite.impl.FavoriteScreen
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteAggregatedScreen
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteFeature
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class FavoriteApi internal constructor(
    private val favoriteStore: FavoriteStore
) {

    fun favoriteListScreen(): Screen = FavoriteScreen()

    fun favoriteAggregatedListScreen(): Screen = FavoriteAggregatedScreen()

    fun updateFavorite(
        id: String,
        isFavorite: Boolean
    ) {
        favoriteStore.accept(
            FavoriteFeature.Msg.Outer.UpdateFavorite(id, isFavorite)
        )
    }

    fun observeFavoriteUpdates(): Flow<FavoriteUpdate> =
        favoriteStore.effects
            .mapNotNull { eff ->
                when (eff) {
                    is FavoriteFeature.Eff.Outer.ItemUpdate.Started -> eff.item
                    is FavoriteFeature.Eff.Outer.ItemUpdate.Error -> eff.item.run { copy(isFavorite = !isFavorite) }
                    else -> null
                }
            }

}
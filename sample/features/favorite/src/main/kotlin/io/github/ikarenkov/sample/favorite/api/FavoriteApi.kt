package io.github.ikarenkov.sample.favorite.api

import com.github.terrakok.modo.Screen
import io.github.ikarenkov.sample.favorite.impl.FavoriteSimpleScreen
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteAggregatedScreen
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteInteractionFeature
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteInteractionStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class FavoriteApi internal constructor(
    private val favoriteInteractionStore: FavoriteInteractionStore
) {

    fun favoriteListScreen(): Screen = FavoriteSimpleScreen()

    fun favoriteAggregatedListScreen(): Screen = FavoriteAggregatedScreen()

    fun updateFavorite(
        id: String,
        isFavorite: Boolean
    ) {
        favoriteInteractionStore.accept(
            FavoriteInteractionFeature.Msg.Outer.UpdateFavorite(id, isFavorite)
        )
    }

    fun observeFavoriteUpdates(): Flow<FavoriteUpdate> =
        favoriteInteractionStore.effects
            .mapNotNull { eff ->
                when (eff) {
                    is FavoriteInteractionFeature.Eff.Outer.ItemUpdate.Started ->
                        eff.item
                    is FavoriteInteractionFeature.Eff.Outer.ItemUpdate.Error ->
                        eff.item.run { copy(isFavorite = !isFavorite) }
                    else -> null
                }
            }

}
package io.github.ikarenkov.sample.favorite.api

import com.github.terrakok.modo.Screen
import io.github.ikarenkov.sample.favorite.impl.FavoriteScreen
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteAggregatedScreen
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteInteractionFeature
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteInteractionStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

class FavoriteApi internal constructor(
    private val favoriteInteractionStore: FavoriteInteractionStore
) {

    fun favoriteListScreen(): Screen = FavoriteScreen()

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
            .filterIsInstance<FavoriteInteractionFeature.Eff.Outer.ItemUpdateFinished>()
            .map { it.update }

}
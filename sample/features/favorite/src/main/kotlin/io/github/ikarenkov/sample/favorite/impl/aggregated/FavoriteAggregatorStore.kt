package io.github.ikarenkov.sample.favorite.impl.aggregated

import io.github.ikarenkov.kombucha.aggregator.AggregatorStore
import io.github.ikarenkov.sample.core.pagination.PaginationMsg
import io.github.ikarenkov.sample.core.pagination.PaginationState
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteAggregatedFeature.Eff
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteAggregatedFeature.Msg
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteAggregatedFeature.State
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull

internal class FavoriteAggregatorStore(
    private val paginationStore: FavoritePaginationStore,
    private val interactionStore: FavoriteStore,
) : AggregatorStore<Msg, State, Eff>(
    name = "FavoriteAggregatorStore"
) {
    override val state: StateFlow<State> =
        combineStates(paginationStore.state, interactionStore.state, ::State)

    override val effects: Flow<Eff> = interactionStore.effects
        .filterIsInstance<FavoriteFeature.Eff.Outer>()
        .mapNotNull {
            val paginationState = paginationStore.state.value
            if (paginationState.hadSuccessLoading) {
                Eff.FavoriteInteraction(it)
            } else {
                null
            }
        }

    init {
        bindEffToMsg(interactionStore, paginationStore) { eff ->
            when (eff) {
                is FavoriteFeature.Eff.Outer.ItemUpdate.Finished -> {
                    val (id, isFavorite) = eff.item
                    if (isFavorite) {
                        val paginationState = paginationStore.state.value
                        if (paginationState.hadSuccessLoading) {
                            PaginationMsg.Outer.AddItem(FavoriteItem(id = id))
                        } else {
                            null
                        }
                    } else {
                        PaginationMsg.Outer.RemoveItem {
                            it.id == id
                        }
                    }
                }
                is FavoriteFeature.Eff.Outer.ItemUpdate.Started -> {
                    val (id, isFavorite) = eff.item
                    if (isFavorite) {
                        PaginationMsg.Outer.AddItem(FavoriteItem(id = id))
                    } else {
                        PaginationMsg.Outer.UpdateItems<FavoriteItem> { item ->
                            if (item.id == id) {
                                item.copy(
                                    isFavorite = false,
                                    updatingFavorite = true
                                )
                            } else {
                                item
                            }
                        }
                    }
                }
                is FavoriteFeature.Eff.Outer.ItemUpdate.Error -> {
                    PaginationMsg.Outer.UpdateItems { item ->
                        if (item.id == eff.item.id) {
                            item.copy(
                                isFavorite = !eff.item.isFavorite,
                                updatingFavorite = false
                            )
                        } else {
                            item
                        }
                    }
                }
                else -> null
            }
        }
    }

    override fun accept(msg: Msg) {
        when (msg) {
            is Msg.FavoriteInteraction -> interactionStore.accept(msg.msg)
            is Msg.Pagination -> paginationStore.accept(msg.msg)
        }
    }

    override fun close() {
        paginationStore.close()
        super.close()
    }

}

internal object FavoriteAggregatedFeature {
    data class State(
        val pagination: PaginationState<FavoriteItem>,
        val favoriteInteraction: FavoriteFeature.State
    )

    sealed interface Msg {
        data class Pagination(val msg: PaginationMsg.Outer<FavoriteItem>) : Msg
        data class FavoriteInteraction(val msg: FavoriteFeature.Msg.Outer) : Msg

        companion object {
            fun Msg(msg: PaginationMsg.Outer<FavoriteItem>) = Pagination(msg)
            fun Msg(msg: FavoriteFeature.Msg.Outer) = FavoriteInteraction(msg)
        }
    }

    sealed interface Eff {
        data class FavoriteInteraction(val eff: FavoriteFeature.Eff.Outer) : Eff
    }
}
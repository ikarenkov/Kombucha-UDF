package io.github.ikarenkov.sample.favorite.impl.aggregated

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.eff_handler.adaptCast
import io.github.ikarenkov.kombucha.reducer.ResultBuilder
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.CoroutinesStore
import io.github.ikarenkov.sample.favorite.api.FavoriteUpdate
import io.github.ikarenkov.sample.favorite.impl.FavoriteAnalytics
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteInteractionFeature.Eff
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteInteractionFeature.Msg
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteInteractionFeature.State
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteInteractionFeature.reducer
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteItem
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class FavoriteInteractionStore(
    favoriteEffectHandler: FavoriteEffectHandler,
    favoriteAnalytics: FavoriteAnalytics
) : CoroutinesStore<Msg, State, Eff>(
    name = "FavoriteInteraction",
    initialState = State(emptyMap()),
    reducer = reducer,
    initialEffects = setOf(Eff.Inner.ObserveFavUpdates),
    effectHandlers = listOf(favoriteEffectHandler.adaptCast())
) {

    init {
        coroutinesScope.launch {
            storeUpdates.collect { (msg, oldState, newState, effects) ->
                when (msg) {
                    is Msg.Outer.ItemClick -> {
                        favoriteAnalytics.itemClick(msg.id, isFavorite = true)
                    }
                    is Msg.Outer.UpdateFavorite -> {
                        favoriteAnalytics.changeFavoriteClick(msg.id, desiredFavorite = msg.isFavorite)
                    }
                    else -> Unit
                }
            }
        }
    }

}

object FavoriteInteractionFeature {

    val reducer = dslReducer<Msg, State, Eff> { msg ->
        when (msg) {
            is Msg.Outer -> outerReducer(msg)
            is Msg.Inner -> innerReducer(msg)
        }
    }

    private fun ResultBuilder<State, Eff>.innerReducer(msg: Msg.Inner) {
        when (msg) {
            is Msg.Inner.ItemRemoveResult.Done -> {
                state {
                    this - msg.id
                }
                eff(Eff.Outer.ItemUpdateFinished(FavoriteUpdate(msg.id, isFavorite = false)))
            }
            is Msg.Inner.ItemRemoveResult.Error -> {
                state {
                    this - msg.id
                }
                eff(Eff.Outer.ItemUpdateError(msg.id, previousFavorite = true))
            }
            is Msg.Inner.AddItem -> {
                eff(Eff.Outer.ItemUpdateFinished(FavoriteUpdate(msg.item.id, isFavorite = true)))
            }
        }
    }

    private fun ResultBuilder<State, Eff>.outerReducer(msg: Msg.Outer) {
        when (msg) {
            is Msg.Outer.UpdateFavorite -> {
                val isAlreadyRemoving = state.removingItems[msg.id] != null
                if (!isAlreadyRemoving) {
                    val item = FavoriteItem(
                        id = msg.id,
                        isFavorite = false,
                        updatingFavorite = true
                    )
                    state {
                        this + item
                    }
                    eff(
                        Eff.Inner.RemoveItem(msg.id),
                        Eff.Outer.ItemUpdateStarted(FavoriteUpdate(item.id, item.isFavorite)),
                    )
                }
            }
            is Msg.Outer.ItemClick -> eff(Eff.Outer.ItemClick(msg.id))
        }
    }

    private infix operator fun State.minus(id: String): State =
        copy(removingItems - id)

    private infix operator fun State.plus(item: FavoriteItem): State =
        copy(removingItems + (item.id to item))

    data class State(
        val removingItems: Map<String, FavoriteItem>
    )

    sealed interface Eff {

        sealed interface Outer : Eff {
            data class ItemUpdateStarted(val update: FavoriteUpdate) : Outer
            data class ItemUpdateFinished(val update: FavoriteUpdate) : Outer

            /**
             * Send when we failed to update item. We also send ItemUpdate to reset isFavorite to previous state
             */
            data class ItemUpdateError(val id: String, val previousFavorite: Boolean) : Outer
            data class ItemClick(val id: String) : Outer
        }

        sealed interface Inner : Eff {
            data class RemoveItem(val id: String) : Inner
            data object ObserveFavUpdates : Inner
        }
    }

    sealed interface Msg {

        sealed interface Outer : Msg {
            data class ItemClick(val id: String) : Outer
            data class UpdateFavorite(val id: String, val isFavorite: Boolean) : Outer
        }

        sealed interface Inner : Msg {

            data class AddItem(val item: FavoriteItem) : Inner
            sealed interface ItemRemoveResult : Inner {
                val id: String

                data class Done(
                    override val id: String,
                ) : ItemRemoveResult

                data class Error(
                    override val id: String,
                    val throwable: Throwable?
                ) : ItemRemoveResult
            }
        }
    }

}

internal class FavoriteEffectHandler(
    private val favoriteRepository: FavoriteRepository
) : EffectHandler<Eff.Inner, Msg.Inner> {

    override fun handleEff(eff: Eff.Inner): Flow<Msg.Inner> = when (eff) {
        Eff.Inner.ObserveFavUpdates -> favoriteRepository.newFavoriteSource().map {
            Msg.Inner.AddItem(it)
        }
        is Eff.Inner.RemoveItem -> flow {
            val msg = favoriteRepository.removeFavoriteItem(eff.id).fold(
                onSuccess = {
                    Msg.Inner.ItemRemoveResult.Done(eff.id)
                },
                onFailure = {
                    Msg.Inner.ItemRemoveResult.Error(eff.id, it)
                }
            )
            emit(msg)
        }
    }

}
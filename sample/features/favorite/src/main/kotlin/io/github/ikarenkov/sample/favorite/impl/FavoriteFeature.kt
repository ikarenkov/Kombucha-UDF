package io.github.ikarenkov.sample.favorite.impl

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.eff_handler.adaptCast
import io.github.ikarenkov.kombucha.reducer.ResultBuilder
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreFactory
import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature.Eff
import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature.Msg
import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature.State
import io.github.ikarenkov.sample.favorite.impl.core.LCE
import io.github.ikarenkov.sample.favorite.impl.core.toLce
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteItem
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteRepository
import io.github.ikarenkov.sample.favorite.impl.data.changeFavorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class FavoriteStore(
    storeFactory: StoreFactory,
    effectHandler: FavoriteEffHandler,
) : Store<Msg, State, Eff> by storeFactory.create(
    name = "FavoriteStore",
    reducer = FavoriteFeature.reducer,
    initialState = State(LCE.Loading()),
    initialEffects = setOf(Eff.Inner.LoadFav, Eff.Inner.ObserveFavUpdates),
    effectHandlers = arrayOf(effectHandler.adaptCast())
)

internal object FavoriteFeature {

    val reducer = dslReducer<Msg, State, Eff> { msg ->
        when (msg) {
            is Msg.Outer -> outerReducer(msg)
            is Msg.Inner -> innerReducer(msg)
        }
    }

    private fun ResultBuilder<State, Eff>.innerReducer(msg: Msg.Inner) {
        when (msg) {
            is Msg.Inner.ItemLoadingResult -> state { State(msg.result.toLce()) }
            is Msg.Inner.ItemRemoveResult.Done -> {
                state {
                    removeItem(msg.id)
                }
                eff(Eff.Outer.ItemRemoved(msg.id))
            }
            is Msg.Inner.ItemRemoveResult.Error -> {
                state {
                    map {
                        map { item ->
                            if (item.id == msg.id) {
                                item.copy(isFavorite = true, updatingFavorite = false)
                            } else {
                                item
                            }
                        }
                    }
                }
                eff(Eff.Outer.ItemRemoveError(msg.id))
            }
            is Msg.Inner.AddItem -> {
                if (state.content is LCE.Data<*>) {
                    state { addItem(msg.item) }
                    eff(Eff.Outer.ItemAdded(msg.item.id))
                }
            }
        }
    }

    private fun ResultBuilder<State, Eff>.outerReducer(msg: Msg.Outer) {
        when (msg) {
            is Msg.Outer.RemoveFavorite -> {
                var isAlreadyRemoving = false
                state {
                    map {
                        map { item ->
                            if (item.id == msg.id) {
                                isAlreadyRemoving = item.updatingFavorite
                                item.changeFavorite()
                            } else {
                                item
                            }
                        }
                    }
                }
                if (!isAlreadyRemoving) {
                    eff(Eff.Inner.RemoveItem(msg.id))
                }
            }
            is Msg.Outer.RetryLoad -> {
                if (state.content is LCE.Error && !state.content.inProgress) {
                    state { State(LCE.Loading()) }
                    eff(Eff.Inner.LoadFav)
                }
            }
            is Msg.Outer.ItemClick -> eff(Eff.Outer.ItemClick(msg.id))
        }
    }

    private fun State.map(
        action: List<FavoriteItem>.() -> List<FavoriteItem>,
    ): State = if (content is LCE.Data) {
        val updatedItems = action.invoke(content.value)
        copy(content = content.copy(value = updatedItems))
    } else {
        this
    }

    private infix operator fun <T> T.plus(list: List<T>): List<T> =
        mutableListOf(this).also { it.addAll(list) }

    private fun State.addItem(item: FavoriteItem): State = map {
        val inListItem = find { it.id == item.id }
        // ignoring if ve already have an item. But it need to be changed
        if (inListItem == null || !inListItem.isFavorite && inListItem.updatingFavorite) {
            item + this
        } else {
            this
        }
    }

    private fun State.removeItem(id: String): State =
        map {
            filterNot { it.id == id }
        }

    sealed interface Eff {

        sealed interface Outer : Eff {
            data class ItemAdded(val id: String) : Outer
            data class ItemRemoved(val id: String) : Outer
            data class ItemRemoveError(val id: String) : Outer
            data class ItemClick(val id: String) : Outer
        }

        sealed interface Inner : Eff {
            data object LoadFav : Inner
            data class RemoveItem(val id: String) : Inner
            data object ObserveFavUpdates : Inner
        }
    }

    sealed interface Msg {

        sealed interface Outer : Msg {
            data class ItemClick(val id: String) : Outer
            data class RemoveFavorite(val id: String) : Outer
            data object RetryLoad : Outer
        }

        sealed interface Inner : Msg {

            data class AddItem(val item: FavoriteItem) : Inner
            data class ItemLoadingResult(val result: Result<List<FavoriteItem>>) : Inner
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

    internal data class State(
        val content: LCE<List<FavoriteItem>>,
    )
}

internal class FavoriteEffHandler(
    private val repository: FavoriteRepository,
) : EffectHandler<Eff.Inner, Msg.Inner> {

    override fun handleEff(eff: Eff.Inner): Flow<Msg.Inner> = when (eff) {
        is Eff.Inner.LoadFav -> flow {
            emit(
                Msg.Inner.ItemLoadingResult(repository.loadFavoriteItems())
            )
        }
        is Eff.Inner.RemoveItem -> flow {
            emit(
                repository.removeFavoriteItem(eff.id).fold(
                    onSuccess = { Msg.Inner.ItemRemoveResult.Done(eff.id) },
                    onFailure = { Msg.Inner.ItemRemoveResult.Error(eff.id, null) }
                )
            )
        }
        Eff.Inner.ObserveFavUpdates -> repository.newFavoriteSource().map {
            Msg.Inner.AddItem(it)
        }
    }

}

package io.github.ikarenkov.sample.favorite.impl

import io.github.ikarenkov.kombucha.test.testReducer
import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature.Eff
import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature.Msg
import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature.State
import io.github.ikarenkov.sample.favorite.impl.core.LCE
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteItem
import kotlin.test.Test

class FavoriteFeatureTest {

    private val initialItems = List(5) { FavoriteItem(it.toString(), "Item: $it") }

    @Test
    fun `Test initial loading`() {
        testReducer(
            initialState = State(LCE.Loading()),
            reducer = FavoriteFeature.reducer
        ) {
            Msg.Inner.ItemLoadingResult(Result.success(initialItems)) assertReturn State(LCE.Data(initialItems))
        }
    }

    @Test
    fun `Test add item before loading result`() {
        testReducer(
            initialState = State(LCE.Loading()),
            reducer = FavoriteFeature.reducer
        ) {
            val newItem = FavoriteItem("new", "new")
            Msg.Outer.RemoveFavorite(newItem.id) assertReturn
                    State(LCE.Loading()) + setOf(Eff.Inner.RemoveItem(newItem.id))

            val items = List(5) { FavoriteItem(it.toString(), "Item: $it") }
            Msg.Inner.ItemLoadingResult(Result.success(items)) assertReturn State(LCE.Data(items))
        }
    }

    @Test
    fun `When remove item error - Then restore item`() {
        testReducer(
            initialState = State(LCE.Data(initialItems)),
            reducer = FavoriteFeature.reducer
        ) {
            val removeItem = initialItems[1]

            Msg.Outer.RemoveFavorite(removeItem.id) assertReturn {
                val removeResult = initialItems.toMutableList().apply { set(1, removeItem.copy(isFavorite = false, updatingFavorite = true)) }
                State(LCE.Data(removeResult)) + Eff.Inner.RemoveItem(removeItem.id)
            }

            Msg.Inner.ItemRemoveResult.Error(removeItem.id, null) assertReturn
                    State(LCE.Data(initialItems)) + Eff.Outer.ItemRemoveError(removeItem.id)
        }
    }

}
package io.github.ikarenkov.sample.favorite.impl.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import logcat.logcat
import java.io.IOException
import kotlin.coroutines.coroutineContext
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

// Repository, simulating api working with api.
internal class DemoFavRepository {

    companion object {
        private val LOAD_DELAY = 3.seconds
        private val NEW_ITEM_PERIOD = 5.seconds
        private const val ITEMS_COUNT = 5
        private const val ID_TOP_BOUND = 99_999
    }

    suspend fun loadFavoriteItems(): List<FavoriteItem> {
        delay(LOAD_DELAY)
        return List(ITEMS_COUNT) { generateItem() }
    }

    @Suppress("detekt.UnusedPrivateMember")
    suspend fun removeFavoriteItem(id: String) = kotlin.runCatching {
        withContext(Dispatchers.IO) {
            logcat { "removing item with id $id" }
            delay(2.seconds)
            if (Random.nextBoolean()) {
                throw IOException("Emulate error of removing item.")
            }
        }
    }

    fun newFavoriteSource(): Flow<FavoriteItem> = flow {
        while (coroutineContext.isActive) {
            delay(NEW_ITEM_PERIOD)
            emit(generateItem())
        }
    }

    private fun generateItem(): FavoriteItem {
        val id = (0..ID_TOP_BOUND).random().toString()
        return FavoriteItem(
            id = id,
            title = "Item #$id",
            isFavorite = true
        )
    }

}

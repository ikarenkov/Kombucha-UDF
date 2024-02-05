package io.github.ikarenkov.kombucha.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Turn cold flow to hot and caches values only if there are no subscribers.
 */
@ExperimentalCoroutinesApi
fun <T> Flow<T>.cacheWhenNoSubscribers(scope: CoroutineScope): Flow<T> {
    val cache = MutableSharedFlow<T>(replay = Int.MAX_VALUE)
    scope.launch {
        collect { cache.emit(it) }
    }
    return cache.onEach {
        if (cache.subscriptionCount.value != 0) {
            cache.resetReplayCache()
        }
    }
}

package io.github.ikarenkov.kombucha.ui

import io.github.ikarenkov.kombucha.DefaultStoreCoroutineExceptionHandler
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive

/**
 * Wrapper for store, that allows handle basic UI scenarios:
 * 1. Convert models to Ui Models
 * 2. Cache ui effects when there is no subscribers and emit cached effects with a first subscription
 * @param propagateCloseToOriginal shows if we need to call [Store.close] on the [store] when this store is closed.
 */
class UiStore<UiMsg : Any, UiState : Any, UiEff : Any, Msg : Any, State : Any, Eff : Any>(
    private val store: Store<Msg, State, Eff>,
    private val uiMsgToMsgConverter: (UiMsg) -> Msg,
    private val uiStateConverter: (State) -> UiState,
    private val uiEffConverter: (Eff) -> UiEff?,
    private val propagateCloseToOriginal: Boolean = true,
    coroutineExceptionHandler: CoroutineExceptionHandler = DefaultStoreCoroutineExceptionHandler(),
    uiDispatcher: CoroutineDispatcher = Dispatchers.Main,
    cacheUiEffects: Boolean = true,
) : Store<UiMsg, UiState, UiEff> {

    private val coroutineScope = StoreScope(
        name = "UiStore for $store",
        coroutineExceptionHandler = coroutineExceptionHandler,
        coroutineContext = uiDispatcher
    )

    override val state: StateFlow<UiState> = store.state
        .map { uiStateConverter(it) }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = uiStateConverter(store.state.value)
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    override val effects: Flow<UiEff> =
        store.effects
            .mapNotNull { uiEffConverter(it) }
            .let { originalFlow ->
                if (cacheUiEffects) {
                    originalFlow.cacheWhenNoSubscribers(coroutineScope)
                } else {
                    originalFlow
                }
            }
    override val isActive: Boolean get() = coroutineScope.isActive

    override fun accept(msg: UiMsg) {
        store.accept(uiMsgToMsgConverter(msg))
    }

    override fun close() {
        coroutineScope.cancel()
        if (propagateCloseToOriginal) {
            store.close()
        }
    }

}
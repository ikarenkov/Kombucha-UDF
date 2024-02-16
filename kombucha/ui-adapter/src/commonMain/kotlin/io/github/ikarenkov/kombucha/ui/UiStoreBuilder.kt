package io.github.ikarenkov.kombucha.ui

import io.github.ikarenkov.kombucha.store.Store
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Builder that helps to avoid explicit declaration of generic types in [UiStore] and provides different builder functions to build [UiStore].
 * You can
 */
class UiStoreBuilder<Msg : Any, State : Any, Eff : Any>(
    val store: Store<Msg, State, Eff>
) {

    fun <UiMsg : Any, UiState : Any, UiEff : Any> using(
        uiMsgToMsgConverter: (UiMsg) -> Msg,
        uiStateConverter: (State) -> UiState,
        uiEffConverter: (Eff) -> UiEff?,
        cacheUiEffects: Boolean = true,
        propagateCloseToOriginal: Boolean = true,
        uiDispatcher: CoroutineDispatcher = Dispatchers.Main,
    ): UiStore<UiMsg, UiState, UiEff, Msg, State, Eff> = UiStore(
        store = store,
        uiMsgToMsgConverter = uiMsgToMsgConverter,
        uiStateConverter = uiStateConverter,
        uiEffConverter = uiEffConverter,
        propagateCloseToOriginal = propagateCloseToOriginal,
        cacheUiEffects = cacheUiEffects,
        uiDispatcher = uiDispatcher
    )

    /**
     * Implementation based on inheritance UiMsg from Msg and UiEff from Eff
     */
    inline fun <UiMsg : Msg, UiState : Any, reified UiEff : Eff> using(
        cacheUiEffects: Boolean = true,
        propagateCloseToOriginal: Boolean = true,
        uiDispatcher: CoroutineDispatcher = Dispatchers.Main,
        noinline uiStateConverter: (State) -> UiState,
    ): UiStore<UiMsg, UiState, UiEff, Msg, State, Eff> = UiStore(
        store = store,
        uiMsgToMsgConverter = { it },
        uiStateConverter = uiStateConverter,
        uiEffConverter = { it as? UiEff },
        uiDispatcher = uiDispatcher,
        propagateCloseToOriginal = propagateCloseToOriginal,
        cacheUiEffects = cacheUiEffects
    )

}

/**
 * Dsl builder that helps to avoid full declaration of generic types using regular constructor of [UiStore].
 */
fun <Msg : Any, State : Any, Eff : Any> Store<Msg, State, Eff>.uiBuilder() = UiStoreBuilder(this)

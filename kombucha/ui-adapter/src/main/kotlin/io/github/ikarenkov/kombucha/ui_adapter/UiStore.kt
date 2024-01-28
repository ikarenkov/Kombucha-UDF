package io.github.ikarenkov.kombucha.ui_adapter

import io.github.ikarenkov.kombucha.store.CoroutinesStore
import io.github.ikarenkov.kombucha.store.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UiStore<UiMsg : Any, UiState : Any, UiEff : Any, Msg : Any, State : Any, Eff : Any>(
    private val store: CoroutinesStore<Msg, State, Eff>,
    private val uiMsgConverter: (UiMsg) -> Msg,
    private val uiStateConverter: (State) -> UiState,
    private val uiEffConverter: (Eff) -> UiEff,
) : Store<UiMsg, UiState, UiEff> {

    override val state: StateFlow<UiState> = store.state
        .map { uiStateConverter(it) }
        .stateIn(
            scope = store.coroutinesScope,
            started = SharingStarted.Lazily,
            initialValue = uiStateConverter(store.state.value)
        )
    override val effects: Flow<UiEff> = store.effects.map { uiEffConverter(it) }

    override fun accept(msg: UiMsg) {
        store.accept(uiMsgConverter(msg))
    }

    override fun cancel() {
        store.cancel()
    }

}
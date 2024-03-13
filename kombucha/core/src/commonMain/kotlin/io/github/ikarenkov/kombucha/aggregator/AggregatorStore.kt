package io.github.ikarenkov.kombucha.aggregator

import io.github.ikarenkov.kombucha.DefaultStoreCoroutineExceptionHandler
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

abstract class AggregatorStore<Msg : Any, State : Any, Eff : Any>(
    protected val coroutineScope: CoroutineScope,
) : Store<Msg, State, Eff> {

    override val isActive: Boolean get() = coroutineScope.isActive

    constructor(
        name: String? = null,
        coroutineExceptionHandler: CoroutineExceptionHandler = DefaultStoreCoroutineExceptionHandler()
    ) : this(StoreScope(name, coroutineExceptionHandler))

    override fun close() {
        coroutineScope.cancel()
    }

    protected fun <Eff : Any, Msg : Any> bindEffToMsg(
        storeEff: Store<*, *, Eff>,
        storeMsg: Store<Msg, *, *>,
        transform: (Eff) -> Msg?
    ) {
        coroutineScope.launch {
            bindFlowToMsg(storeEff.effects, storeMsg, transform)
        }
    }

    protected fun <State : Any, Msg : Any> bindStateToMsg(
        storeEff: Store<*, State, *>,
        storeMsg: Store<Msg, *, *>,
        transform: (State) -> Msg?
    ) {
        coroutineScope.launch {
            bindFlowToMsg(storeEff.state, storeMsg, transform)
        }
    }

    protected fun <T1, T2, R> combineStates(
        state1: StateFlow<T1>,
        state2: StateFlow<T2>,
        transform: (T1, T2) -> R
    ): StateFlow<R> = combine(state1, state2, transform)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = transform(state1.value, state2.value)
        )

    protected fun <T1, T2, T3, R> combineStates(
        state1: StateFlow<T1>,
        state2: StateFlow<T2>,
        state3: StateFlow<T3>,
        transform: (T1, T2, T3) -> R
    ): StateFlow<R> = combine(state1, state2, state3, transform)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = transform(state1.value, state2.value, state3.value)
        )

    protected fun <T1, T2, T3, T4, R> combineStates(
        state1: StateFlow<T1>,
        state2: StateFlow<T2>,
        state3: StateFlow<T3>,
        state4: StateFlow<T4>,
        transform: (T1, T2, T3, T4) -> R
    ): StateFlow<R> = combine(state1, state2, state3, state4, transform)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = transform(state1.value, state2.value, state3.value, state4.value)
        )

    protected fun <T1, T2, T3, T4, T5, R> combineStates(
        state1: StateFlow<T1>,
        state2: StateFlow<T2>,
        state3: StateFlow<T3>,
        state4: StateFlow<T4>,
        state5: StateFlow<T5>,
        transform: (T1, T2, T3, T4, T5) -> R
    ): StateFlow<R> = combine(state1, state2, state3, state4, state5, transform)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = transform(state1.value, state2.value, state3.value, state4.value, state5.value)
        )

}
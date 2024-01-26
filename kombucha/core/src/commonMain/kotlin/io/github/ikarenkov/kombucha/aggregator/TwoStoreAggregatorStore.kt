package io.github.ikarenkov.kombucha.aggregator

import io.github.ikarenkov.kombucha.store.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

class TwoStoreAggregatorStore<
        Msg1 : Any,
        State1 : Any,
        Eff1 : Any,
        Msg2 : Any,
        State2 : Any,
        Eff2 : Any,
        >(
    private val store1: Store<Msg1, State1, Eff1>,
    private val store2: Store<Msg2, State2, Eff2>,
) : AggregatorStore<Either<Msg1, Msg2>, Pair<State1, State2>, Either<Eff1, Eff2>>() {

    override val state: StateFlow<Pair<State1, State2>> =
        combine(store1.state, store2.state, ::Pair).stateIn(
            scope,
            started = SharingStarted.Lazily,
            store1.state.value to store2.state.value
        )

    override val effects: Flow<Either<Eff1, Eff2>> =
        merge(
            store1.effects.map { Either.Left(it) },
            store2.effects.map { Either.Right(it) }
        )

    override fun accept(msg: Either<Msg1, Msg2>) {
        when (msg) {
            is Either.Left -> store1.accept(msg.value)
            is Either.Right -> store2.accept(msg.value)
        }
    }

    override fun cancel() {
        store1.cancel()
        store2.cancel()
    }


}

sealed interface Either<T1, T2> {
    data class Left<T1, T2>(val value: T1) : Either<T1, T2>
    data class Right<T1, T2>(val value: T2) : Either<T1, T2>
}
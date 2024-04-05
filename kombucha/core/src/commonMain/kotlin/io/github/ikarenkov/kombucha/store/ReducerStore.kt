package io.github.ikarenkov.kombucha.store

import kotlinx.coroutines.flow.SharedFlow

/**
 * Extension of the store witch provides [reducerUpdates] that describes a work of one cycle of reducer.
 * Interfaces segregation is made for simplification - you don't need store updates in every [Store] implementation.
 * F.e. look at [AggregatorStore](../aggregator/AggregatorStore.kt) witch is unable to implements this interface.
 */
interface ReducerStore<Msg : Any, State : Any, Eff : Any> : Store<Msg, State, Eff> {

    val reducerUpdates: SharedFlow<ReducerUpdate<Msg, State, Eff>>

}

package io.github.ikarenkov.kombucha.store

/**
 * Represents one cycle of work of reducer. It contains reducers inputs and outputs in one place.
 */
data class ReducerUpdate<Msg, State, Eff>(
    val msg: Msg,
    val oldState: State,
    val newState: State,
    val effects: Set<Eff>
)
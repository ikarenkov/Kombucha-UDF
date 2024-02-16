package io.github.ikarenkov.kombucha.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * The base component of library that hold state and converst incoming [Msg] to a new [State] and [Eff].
 * Take a look and the main implementation: [CoroutinesStore]
 */
@OptIn(ExperimentalStdlibApi::class)
interface Store<Msg : Any, State : Any, Eff : Any> : AutoCloseable {

    /**
     * Represent current state of this store. Can be modified only through [accept].
     */
    val state: StateFlow<State>

    /**
     * Represents a flow of fire and forget events.
     */
    val effects: Flow<Eff>

    /**
     * Indicates if store is active and can accept messages.
     */
    val isActive: Boolean

    /**
     * Accepts given [msg] and send it to the [Reducer] with a current state from [state].
     * Then a new state is published to [state] and provided effects are send to the [effects] along with
     * handling them with [EffectHandler] if presented.
     * @throws IllegalStateException if called on canceled store. Other words its
     */
    fun accept(msg: Msg)

}

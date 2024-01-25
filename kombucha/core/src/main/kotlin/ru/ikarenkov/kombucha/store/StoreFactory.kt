package ru.ikarenkov.kombucha.store

import ru.ikarenkov.kombucha.eff_handler.FlowEffectHandler

/**
 * Creates instances of [Store]s using the provided components.
 * You can create different [Store] wrappers and combine them depending on circumstances.
 */
interface StoreFactory {

    /**
     * Creates an implementation of [Store].
     *
     * @param name a name of the [Store] being created, used for logging, time traveling, etc.
     */
    fun <Msg : Any, State : Any, Eff : Any> create(
        name: String? = null,
        initialState: State,
        reducer: (State, Msg) -> Pair<State, Set<Eff>>,
        initialEffects: Set<Eff> = setOf(),
        vararg effectHandlers: FlowEffectHandler<Eff, Msg>
    ): Store<Msg, State, Eff>

}

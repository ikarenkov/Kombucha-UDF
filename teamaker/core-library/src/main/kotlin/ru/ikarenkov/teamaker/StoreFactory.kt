package ru.ikarenkov.teamaker

/**
 * Creates instances of [Store]s using the provided components.
 * You can create different [Store] wrappers and combine them depending on circumstances.
 */
interface StoreFactory {

    /**
     * Creates an implementation of [Store].
     * Must be called only on the main thread if [isAutoInit] argument is true (default).
     * Can be called on any thread if the [isAutoInit] is false.
     *
     * @param name a name of the [Store] being created, used for logging, time traveling, etc.
     * @param isAutoInit if `true` then the [Store] will be automatically initialized after creation,
     * otherwise call [Store.init] manually
     */
    fun <Msg : Any, State : Any, Eff : Any> create(
        name: String? = null,
        initialState: State,
        reducer: (State, Msg) -> Pair<State, Set<Eff>>,
        initEffects: Set<Eff> = setOf(),
        vararg effectHandlers: EffectHandler<Eff, Msg>
    ): Store<Msg, State, Eff>

}

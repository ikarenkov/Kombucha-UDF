package ru.ikarenkov.teamaker

class SyncStoreFactory : StoreFactory {

    override fun <Msg : Any, State : Any, Eff : Any> create(
        name: String?,
        initialState: State,
        reducer: (State, Msg) -> Pair<State, Set<Eff>>,
        initEffects: Set<Eff>,
        vararg effectHandlers: EffectHandler<Eff, Msg>
    ): Store<Msg, State, Eff> = SyncStore(
        initialState,
        reducer,
        effectHandlers.toList(),
        initEffects
    )

}
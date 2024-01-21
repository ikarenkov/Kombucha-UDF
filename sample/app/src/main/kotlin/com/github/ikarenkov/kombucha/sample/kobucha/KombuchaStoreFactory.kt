package com.github.ikarenkov.kombucha.sample.kobucha

import kotlinx.coroutines.CoroutineExceptionHandler
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import ru.ikarenkov.teamaker.eff_handler.FlowEffectHandler
import ru.ikarenkov.teamaker.store.CoroutinesStore
import ru.ikarenkov.teamaker.store.Store
import ru.ikarenkov.teamaker.store.StoreFactory

class KombuchaStoreFactory : StoreFactory {
    override fun <Msg : Any, State : Any, Eff : Any> create(
        name: String?,
        initialState: State,
        reducer: (State, Msg) -> Pair<State, Set<Eff>>,
        initEffects: Set<Eff>,
        vararg effectHandlers: FlowEffectHandler<Eff, Msg>
    ): Store<Msg, State, Eff> = CoroutinesStore<Msg, State, Eff>(
        name = name,
        reducer = reducer,
        effHandlers = effectHandlers.toList(),
        initialState = initialState,
        initialEffects = initEffects,
        coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logcat(LogPriority.ERROR) {
                "Unhandled coroutine error in the Store with name \"$name\".\n" + throwable.asLog()
            }
        }
    )

}
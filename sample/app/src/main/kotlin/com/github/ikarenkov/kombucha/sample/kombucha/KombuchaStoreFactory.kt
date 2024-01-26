package com.github.ikarenkov.kombucha.sample.kombucha

import kotlinx.coroutines.CoroutineExceptionHandler
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import com.github.ikarenkov.kombucha.eff_handler.EffectHandler
import com.github.ikarenkov.kombucha.reducer.Reducer
import com.github.ikarenkov.kombucha.store.CoroutinesStore
import com.github.ikarenkov.kombucha.store.Store
import com.github.ikarenkov.kombucha.store.StoreFactory

class KombuchaStoreFactory : StoreFactory {
    override fun <Msg : Any, State : Any, Eff : Any> create(
        name: String?,
        initialState: State,
        reducer: Reducer<Msg, State, Eff>,
        initialEffects: Set<Eff>,
        vararg effectHandlers: EffectHandler<Eff, Msg>
    ): Store<Msg, State, Eff> = CoroutinesStore<Msg, State, Eff>(
        name = name,
        reducer = reducer,
        effectHandlers = effectHandlers.toList(),
        initialState = initialState,
        initialEffects = initialEffects,
        coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logcat(LogPriority.ERROR) {
                "Unhandled coroutine error in the Store with name \"$name\".\n" + throwable.asLog()
            }
        }
    )

}
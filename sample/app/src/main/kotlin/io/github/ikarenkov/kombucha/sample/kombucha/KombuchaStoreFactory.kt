package io.github.ikarenkov.kombucha.sample.kombucha

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.reducer.Reducer
import io.github.ikarenkov.kombucha.sample.BuildConfig
import io.github.ikarenkov.kombucha.store.CoroutinesStore
import io.github.ikarenkov.kombucha.store.ReducerStore
import io.github.ikarenkov.kombucha.store.ReducerStoreFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import logcat.LogPriority
import logcat.logcat

class KombuchaStoreFactory : ReducerStoreFactory {
    override fun <Msg : Any, State : Any, Eff : Any> create(
        name: String?,
        initialState: State,
        reducer: Reducer<Msg, State, Eff>,
        initialEffects: Set<Eff>,
        vararg effectHandlers: EffectHandler<Eff, Msg>
    ): ReducerStore<Msg, State, Eff> = CoroutinesStore<Msg, State, Eff>(
        name = name,
        reducer = reducer,
        effectHandlers = effectHandlers.toList(),
        initialState = initialState,
        initialEffects = initialEffects,
        coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            val debug = BuildConfig.DEBUG
            val crashLogEnd = if (debug) "Crashing" else "Ignoring, it is release"
            logcat(LogPriority.ERROR) {
                "Unhandled coroutine error in the Store with name \"$name\", coroutine name is ${coroutineContext[CoroutineName]}. $crashLogEnd."
            }
            if (debug) {
                throw throwable
            }
        }
    )

}
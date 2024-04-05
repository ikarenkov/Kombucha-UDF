package io.github.ikarenkov.kombucha.store

import io.github.ikarenkov.kombucha.DefaultStoreCoroutineExceptionHandler
import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.reducer.Reducer
import kotlinx.coroutines.CoroutineExceptionHandler

/**
 * Default store factory implementation that provides [CoroutinesStore].
 */
class CoroutinesStoreFactory(
    private val coroutineExceptionHandler: CoroutineExceptionHandler = DefaultStoreCoroutineExceptionHandler()
) : ReducerStoreFactory {
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
        coroutineExceptionHandler = coroutineExceptionHandler
    )
}
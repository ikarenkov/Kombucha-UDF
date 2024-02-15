package io.github.ikarenkov.kombucha.store

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.reducer.Reducer
import kotlinx.coroutines.test.TestScope

class TestStore<Msg : Any, State : Any, Eff : Any>(
    reducer: Reducer<Msg, State, Eff>,
    initialState: State,
    effectHandlers: List<EffectHandler<Eff, Msg>> = listOf(),
    initialEffects: Set<Eff> = setOf(),
) : CoroutinesStore<Msg, State, Eff>(
    name = "TestStore",
    reducer = reducer,
    effectHandlers = effectHandlers,
    initialState = initialState,
    initialEffects = initialEffects
) {
    public override val coroutinesScope: TestScope = TestScope()
}
package ru.ikarenkov.teamaker.sample.counter.impl

import ru.ikarenkov.teamaker.Store
import ru.ikarenkov.teamaker.StoreFactory
import ru.ikarenkov.teamaker.adaptCast
import ru.ikarenkov.teamaker.sample.counter.api.CounterDeps
import ru.ikarenkov.teamaker.sample.counter.api.counterFeatureFacade

internal class CounterStoreFactory(
    private val storeFactory: StoreFactory,
    private val counterEffectHandler: CounterEffectHandler,
    private val deps: CounterDeps
) {

    fun create(initialState: State): Store<Msg, State, Eff> = storeFactory.create(
        "COUNTER",
        initialState,
        rootReducer::invoke,
        emptySet(),
        counterEffectHandler(deps).adaptCast(),
    )

}

internal fun createCounterStore(initialState: State): Store<Msg, State, Eff> =
    counterFeatureFacade.scope.get<CounterStoreFactory>().create(initialState)
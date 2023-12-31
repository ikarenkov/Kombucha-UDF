package ru.ikarenkov.teamaker.sample.counter.impl

import ru.ikarenkov.teamaker.store_legacy.Store
import ru.ikarenkov.teamaker.store_legacy.StoreFactory
import ru.ikarenkov.teamaker.eff_handler_legacy.adaptCast
import ru.ikarenkov.teamaker.sample.counter.api.counterFeatureFacade

internal class CounterStoreFactory(
    private val storeFactory: StoreFactory,
    private val counterEffectHandler: CounterEffectHandler,
) {

    fun create(initialState: State): Store<Msg, State, Eff> = storeFactory.create(
        "COUNTER",
        initialState,
        rootReducer::invoke,
        emptySet(),
        counterEffectHandler.adaptCast(),
    )

}

internal fun createCounterStore(initialState: State): Store<Msg, State, Eff> =
    counterFeatureFacade.scope.get<CounterStoreFactory>().create(initialState)
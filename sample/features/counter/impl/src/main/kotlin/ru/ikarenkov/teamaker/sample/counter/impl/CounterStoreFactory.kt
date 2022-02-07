package ru.ikarenkov.teamaker.sample.counter.impl

import ru.ikarenkov.teamaker.Store
import ru.ikarenkov.teamaker.StoreFactory
import ru.ikarenkov.teamaker.adaptCast
import ru.ikarenkov.teamaker.sample.counter.api.counterFeatureFacade

internal class CounterStoreFactory(
    private val storeFactory: StoreFactory,
    private val counterEffectHandler: CounterEffectHandler
) {

    fun create(): Store<Msg, State, Eff> = storeFactory.create(
        "COUNTER",
        State(0),
        rootReducer::invoke,
        emptySet(),
        counterEffectHandler.adaptCast(),
    )

}

internal fun createCounterStore(): Store<Msg, State, Eff> = counterFeatureFacade.scope.get<CounterStoreFactory>().create()
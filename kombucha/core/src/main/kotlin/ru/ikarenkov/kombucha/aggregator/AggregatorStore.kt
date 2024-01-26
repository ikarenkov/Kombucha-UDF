package ru.ikarenkov.kombucha.aggregator

import kotlinx.coroutines.CoroutineExceptionHandler
import ru.ikarenkov.kombucha.store.Store
import ru.ikarenkov.kombucha.store.StoreScope

abstract class AggregatorStore<Msg : Any, State : Any, Eff : Any>(
    name: String? = null,
    coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }
) : Store<Msg, State, Eff> {

    protected val scope = StoreScope(name, coroutineExceptionHandler)

}
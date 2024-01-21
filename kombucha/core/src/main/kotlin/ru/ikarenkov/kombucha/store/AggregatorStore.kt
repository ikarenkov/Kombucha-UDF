package ru.ikarenkov.kombucha.store

import kotlinx.coroutines.CoroutineExceptionHandler

abstract class AggregatorStore<Msg : Any, State : Any, Eff : Any>(
    name: String? = null,
    coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }
) : Store<Msg, State, Eff> {

    protected val scope = StoreScope(name, coroutineExceptionHandler)

}
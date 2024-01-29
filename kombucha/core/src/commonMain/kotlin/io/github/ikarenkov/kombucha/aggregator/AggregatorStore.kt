package io.github.ikarenkov.kombucha.aggregator

import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope

abstract class AggregatorStore<Msg : Any, State : Any, Eff : Any>(
    protected val coroutineScope: CoroutineScope,
) : Store<Msg, State, Eff> {

    constructor(
        name: String? = null,
        coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }
    ) : this(StoreScope(name, coroutineExceptionHandler))

}
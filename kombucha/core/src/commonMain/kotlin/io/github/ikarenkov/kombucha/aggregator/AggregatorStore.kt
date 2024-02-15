package io.github.ikarenkov.kombucha.aggregator

import io.github.ikarenkov.kombucha.DefaultStoreCoroutineExceptionHandler
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

abstract class AggregatorStore<Msg : Any, State : Any, Eff : Any>(
    protected val coroutineScope: CoroutineScope,
) : Store<Msg, State, Eff> {

    override val isActive: Boolean get() = coroutineScope.isActive

    constructor(
        name: String? = null,
        coroutineExceptionHandler: CoroutineExceptionHandler = DefaultStoreCoroutineExceptionHandler()
    ) : this(StoreScope(name, coroutineExceptionHandler))

    override fun cancel() {
        coroutineScope.cancel()
    }

}
package io.github.ikarenkov.kombucha.sample.counter.api

import io.github.ikarenkov.kombucha.sample.core.feature.featureFacade
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterEffectHandler
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterScreenModel
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterStore

val counterFeatureFacade by lazy {
    featureFacade<CounterDeps, CounterApi>("counter") {
        scoped { CounterApi() }
        factory { CounterEffectHandler(get()) }
        factory { params -> CounterStore(params.get(), get(), get()) }
        factory { params -> CounterScreenModel(params.get()) }
    }
}
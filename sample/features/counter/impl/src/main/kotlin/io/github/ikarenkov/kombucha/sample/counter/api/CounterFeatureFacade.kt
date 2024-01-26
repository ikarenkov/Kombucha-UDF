package io.github.ikarenkov.kombucha.sample.counter.api

import io.github.ikarenkov.kombucha.sample.core.feature.featureFacade
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterEffectHandler
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterScreenModel

val counterFeatureFacade by lazy {
    featureFacade<CounterDeps, CounterApi>("counter") {
        scoped { CounterApi() }
        factory { CounterEffectHandler(get()) }
        factory { params -> CounterFeature(params.get(), get(), get()) }
        factory { params -> CounterScreenModel(params.get()) }
    }
}
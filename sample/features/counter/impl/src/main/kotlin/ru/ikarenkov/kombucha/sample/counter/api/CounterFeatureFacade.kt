package ru.ikarenkov.kombucha.sample.counter.api

import ru.ikarenkov.core.feature.featureFacade
import ru.ikarenkov.kombucha.sample.counter.impl.CounterEffectHandler
import ru.ikarenkov.kombucha.sample.counter.impl.CounterFeature
import ru.ikarenkov.kombucha.sample.counter.impl.CounterScreenModel

val counterFeatureFacade by lazy {
    featureFacade<CounterDeps, CounterApi>("counter") {
        scoped { CounterApi() }
        factory { CounterEffectHandler(get()) }
        factory { params -> CounterFeature(params.get(), get(), get()) }
        factory { params -> CounterScreenModel(params.get()) }
    }
}
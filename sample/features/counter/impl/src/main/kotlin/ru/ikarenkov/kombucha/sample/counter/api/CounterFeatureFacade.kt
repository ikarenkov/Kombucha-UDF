package ru.ikarenkov.kombucha.sample.counter.api

import ru.ikarenkov.core.feature.featureFacade
import ru.ikarenkov.kombucha.sample.counter.impl.CounterCoroutineStoreFactory
import ru.ikarenkov.kombucha.sample.counter.impl.CounterEffectHandler
import ru.ikarenkov.kombucha.sample.counter.impl.CounterFlowEffectHandler
import ru.ikarenkov.kombucha.sample.counter.impl.CounterScreenModel
import ru.ikarenkov.kombucha.sample.counter.impl.CounterStoreFactory

val counterFeatureFacade by lazy {
    featureFacade<CounterDeps, CounterApi>("counter") {
        scoped { CounterStoreFactory(get()) }
        scoped { CounterApi() }
        factory { CounterEffectHandler(get()) }
        factory { CounterFlowEffectHandler(get()) }
        factory { CounterCoroutineStoreFactory(get(), get()) }
        factory { params -> CounterScreenModel(params.get(), get()) }
    }
}
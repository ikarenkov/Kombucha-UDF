package ru.ikarenkov.teamaker.sample.counter.api

import ru.ikarenkov.core.feature.featureFacade
import ru.ikarenkov.teamaker.sample.counter.impl.CounterApiImpl
import ru.ikarenkov.teamaker.sample.counter.impl.CounterEffectHandler
import ru.ikarenkov.teamaker.sample.counter.impl.CounterStoreFactory

val counterFeatureFacade by lazy {
    featureFacade<CounterDeps, CounterApi>("counter") {
        scoped { CounterStoreFactory(get(), get(), get()) }
        scoped<CounterApi> { CounterApiImpl() }
        factory { CounterEffectHandler(get()) }
    }
}
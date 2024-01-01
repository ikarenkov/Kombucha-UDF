package ru.ikarenkov.teamaker.sample.counter.api

import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import ru.ikarenkov.core.feature.featureFacade
import ru.ikarenkov.teamaker.sample.counter.impl.CounterCoroutineStoreFactory
import ru.ikarenkov.teamaker.sample.counter.impl.CounterEffectHandler
import ru.ikarenkov.teamaker.sample.counter.impl.CounterFlowEffectHandler
import ru.ikarenkov.teamaker.sample.counter.impl.CounterScreenModel
import ru.ikarenkov.teamaker.sample.counter.impl.CounterStoreFactory
import ru.ikarenkov.teamaker.sample.counter.impl.State

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
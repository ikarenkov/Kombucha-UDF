package com.github.ikarenkov.kombucha.sample.counter.api

import com.github.ikarenkov.kombucha.sample.core.feature.featureFacade
import com.github.ikarenkov.kombucha.sample.counter.impl.CounterEffectHandler
import com.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature
import com.github.ikarenkov.kombucha.sample.counter.impl.CounterScreenModel

val counterFeatureFacade by lazy {
    featureFacade<CounterDeps, CounterApi>("counter") {
        scoped { CounterApi() }
        factory { CounterEffectHandler(get()) }
        factory { params -> CounterFeature(params.get(), get(), get()) }
        factory { params -> CounterScreenModel(params.get()) }
    }
}
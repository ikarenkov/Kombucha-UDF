package ru.ikarenkov.teamaker.learn_compose.api

import ru.ikarenkov.core.feature.featureFacade
import ru.ikarenkov.teamaker.learn_compose.impl.LearnComposeApiImpl

val learnComposeFeatureFacade by lazy {
    featureFacade<LearnComposeDeps, LearnComposeApi>("learn_compose") {
//        scoped { CounterStoreFactory(get(), get()) }
        scoped<LearnComposeApi> { LearnComposeApiImpl() }
//        factory { CounterEffectHandler(get()) }
    }
}
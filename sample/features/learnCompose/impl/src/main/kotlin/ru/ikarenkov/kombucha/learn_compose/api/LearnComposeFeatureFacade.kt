package ru.ikarenkov.kombucha.learn_compose.api

import ru.ikarenkov.core.feature.featureFacade
import ru.ikarenkov.kombucha.learn_compose.impl.LearnComposeApiImpl

val learnComposeFeatureFacade by lazy {
    featureFacade<LearnComposeDeps, LearnComposeApi>("learn_compose") {
        scoped<LearnComposeApi> { LearnComposeApiImpl() }
    }
}
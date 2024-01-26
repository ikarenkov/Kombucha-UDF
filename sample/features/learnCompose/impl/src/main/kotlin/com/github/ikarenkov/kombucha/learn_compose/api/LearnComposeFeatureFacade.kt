package com.github.ikarenkov.kombucha.learn_compose.api

import com.github.ikarenkov.kombucha.learn_compose.impl.LearnComposeApiImpl
import com.github.ikarenkov.kombucha.sample.core.feature.featureFacade

val learnComposeFeatureFacade by lazy {
    featureFacade<LearnComposeDeps, LearnComposeApi>("learn_compose") {
        scoped<LearnComposeApi> { LearnComposeApiImpl() }
    }
}
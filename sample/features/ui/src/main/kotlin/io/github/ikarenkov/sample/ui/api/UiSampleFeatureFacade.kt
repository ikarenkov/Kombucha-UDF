package io.github.ikarenkov.sample.ui.api

import io.github.ikarenkov.kombucha.sample.core.feature.featureFacade
import io.github.ikarenkov.sample.ui.impl.CachingUiEffectsStore
import io.github.ikarenkov.sample.ui.impl.NavigationEffHandler
import io.github.ikarenkov.sample.ui.impl.UpdatesEffectHandler

val uiSampleFeatureFacade by lazy {
    featureFacade<UiSampleDeps, UiSampleApi>("UiSample") {
        scoped { UiSampleApi() }
        factory { UpdatesEffectHandler() }
        factory { NavigationEffHandler(get()) }
        factory { CachingUiEffectsStore(get(), get(), get()) }
    }
}
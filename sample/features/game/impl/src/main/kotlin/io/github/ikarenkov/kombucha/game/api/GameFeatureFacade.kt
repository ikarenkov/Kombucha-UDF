package io.github.ikarenkov.kombucha.game.api

import io.github.ikarenkov.kombucha.game.impl.GameApiImpl
import io.github.ikarenkov.kombucha.sample.core.feature.featureFacade

val gameFeatureFacade by lazy {
    featureFacade<GameDeps, GameApi>("gamedetails") {
        scoped<GameApi> { GameApiImpl() }
    }
}
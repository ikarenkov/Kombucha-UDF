package com.github.ikarenkov.kombucha.game.api

import com.github.ikarenkov.kombucha.game.impl.GameApiImpl
import com.github.ikarenkov.kombucha.sample.core.feature.featureFacade

val gameFeatureFacade by lazy {
    featureFacade<GameDeps, GameApi>("gamedetails") {
        scoped<GameApi> { GameApiImpl() }
    }
}
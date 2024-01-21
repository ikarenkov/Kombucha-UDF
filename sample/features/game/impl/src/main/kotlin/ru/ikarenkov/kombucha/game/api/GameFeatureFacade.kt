package ru.ikarenkov.kombucha.game.api

import ru.ikarenkov.core.feature.featureFacade
import ru.ikarenkov.kombucha.game.impl.GameApiImpl

val gameFeatureFacade by lazy {
    featureFacade<GameDeps, GameApi>("gamedetails") {
        scoped<GameApi> { GameApiImpl() }
    }
}
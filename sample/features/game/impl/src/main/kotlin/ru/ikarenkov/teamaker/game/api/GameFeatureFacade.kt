package ru.ikarenkov.teamaker.game.api

import ru.ikarenkov.core.feature.featureFacade
import ru.ikarenkov.teamaker.game.impl.GameApiImpl

val gameFeatureFacade by lazy {
    featureFacade<GameDeps, GameApi>("gamedetails") {
        scoped<GameApi> { GameApiImpl() }
    }
}
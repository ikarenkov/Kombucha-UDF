package com.github.ikarenkov.kombucha.game.impl

import com.github.terrakok.modo.Screen
import com.github.ikarenkov.kombucha.game.api.GameApi
import com.github.ikarenkov.kombucha.game.impl.details.ui.GameDetailsScreen

internal class GameApiImpl : GameApi {
    override fun createScreen(): Screen = GameDetailsScreen()
}
package ru.ikarenkov.teamaker.game.impl

import com.github.terrakok.modo.Screen
import ru.ikarenkov.teamaker.game.api.GameApi
import ru.ikarenkov.teamaker.game.impl.details.ui.GameDetailsScreen

internal class GameApiImpl : GameApi {
    override fun createScreen(): Screen = GameDetailsScreen()
}
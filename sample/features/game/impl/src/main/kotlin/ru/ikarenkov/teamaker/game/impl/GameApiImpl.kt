package ru.ikarenkov.teamaker.game.impl

import com.github.terrakok.modo.android.compose.ComposeScreen
import ru.ikarenkov.teamaker.game.api.GameApi
import ru.ikarenkov.teamaker.game.impl.details.ui.GameDetailsScreen

internal class GameApiImpl : GameApi {
    override fun createScreen(): ComposeScreen = GameDetailsScreen()
}
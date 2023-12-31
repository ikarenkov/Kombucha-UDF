package ru.ikarenkov.teamaker.learn_compose.impl

import com.github.terrakok.modo.Screen
import ru.ikarenkov.teamaker.learn_compose.api.LearnComposeApi

class LearnComposeApiImpl : LearnComposeApi {

    override fun screen(): Screen = LearnComposeScreen()

}
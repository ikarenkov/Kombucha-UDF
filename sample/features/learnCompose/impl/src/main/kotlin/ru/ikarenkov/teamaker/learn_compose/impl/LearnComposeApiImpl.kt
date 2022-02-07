package ru.ikarenkov.teamaker.learn_compose.impl

import com.github.terrakok.modo.android.compose.ComposeScreen
import ru.ikarenkov.teamaker.learn_compose.api.LearnComposeApi

class LearnComposeApiImpl : LearnComposeApi {

    override fun screen(): ComposeScreen = LearnComposeScreen()

}
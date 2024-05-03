package io.github.ikarenkov.kombucha.learn_compose.impl

import com.github.terrakok.modo.Screen
import io.github.ikarenkov.kombucha.learn_compose.api.LearnComposeApi

class LearnComposeApiImpl : LearnComposeApi {

    override fun screen(): Screen = LearnComposeScreen()

}
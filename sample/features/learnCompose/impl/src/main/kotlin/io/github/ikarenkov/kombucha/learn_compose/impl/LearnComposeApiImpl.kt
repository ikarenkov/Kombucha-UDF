package io.github.ikarenkov.kombucha.learn_compose.impl

import io.github.ikarenkov.kombucha.learn_compose.api.LearnComposeApi
import com.github.terrakok.modo.Screen

class LearnComposeApiImpl : LearnComposeApi {

    override fun screen(): Screen = LearnComposeScreen()

}
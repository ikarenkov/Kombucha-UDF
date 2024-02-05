package io.github.ikarenkov.sample.ui.api

import com.github.terrakok.modo.Screen
import io.github.ikarenkov.sample.ui.impl.CachingUiEffectsScreen
import io.github.ikarenkov.sample.ui.impl.DetailsScreen

class UiSampleApi internal constructor(){

    fun cachingUiEffectsScreen(): Screen = CachingUiEffectsScreen()

    fun detailsScreen(id: String): Screen = DetailsScreen(id)

}
package io.github.ikarenkov.sample.favorite.api

import com.github.terrakok.modo.Screen
import io.github.ikarenkov.sample.favorite.impl.FavoriteScreen

class FavoriteApi internal constructor() {

    fun favoriteListScreen(): Screen = FavoriteScreen()

}
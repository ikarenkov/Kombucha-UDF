package com.github.ikarenkov.sample.shikimori.api

import com.github.ikarenkov.sample.shikimori.impl.AnimesScreen
import com.github.terrakok.modo.Screen

class ShikimoriApi internal constructor() {

    fun createScreen(): Screen = AnimesScreen()
}
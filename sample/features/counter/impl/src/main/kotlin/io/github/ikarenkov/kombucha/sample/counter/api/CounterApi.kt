package io.github.ikarenkov.kombucha.sample.counter.api

import com.github.terrakok.modo.Screen
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterScreen

class CounterApi internal constructor(){

    fun createScreen(): Screen = CounterScreen()

}
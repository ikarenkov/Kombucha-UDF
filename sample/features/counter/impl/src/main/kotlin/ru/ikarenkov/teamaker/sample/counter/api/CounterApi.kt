package ru.ikarenkov.teamaker.sample.counter.api

import com.github.terrakok.modo.Screen
import ru.ikarenkov.teamaker.sample.counter.impl.CounterScreen

class CounterApi internal constructor(){

    fun createScreen(): Screen = CounterScreen()

}
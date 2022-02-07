package ru.ikarenkov.teamaker.sample.counter.impl

import com.github.terrakok.modo.android.compose.ComposeScreen
import ru.ikarenkov.teamaker.sample.counter.api.CounterApi

internal class CounterApiImpl : CounterApi {

    override fun createScreen(): ComposeScreen = CounterScreen()

}
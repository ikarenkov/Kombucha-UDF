package ru.ikarenkov.teamaker.sample.deps

import com.github.terrakok.modo.Forward
import com.github.terrakok.modo.Modo
import ru.ikarenkov.teamaker.sample.counter.api.CounterDeps
import ru.ikarenkov.teamaker.sample.counter.api.counterFeatureFacade

class CounterDepsImpl(
    private val modo: Modo,
) : CounterDeps {

    override fun openNewScreen() {
        modo.dispatch(Forward(counterFeatureFacade.api.createScreen()))
    }

}
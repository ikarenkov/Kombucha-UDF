package com.github.ikarenkov.kombucha.sample.deps

import com.github.ikarenkov.kombucha.sample.NavigationHolder
import com.github.terrakok.modo.stack.Forward
import ru.ikarenkov.teamaker.sample.counter.api.CounterDeps
import ru.ikarenkov.teamaker.sample.counter.api.counterFeatureFacade

class CounterDepsImpl(
    val navigationHolder: NavigationHolder
) : CounterDeps {

    override fun openNewScreen() {
        navigationHolder.rootScreen?.screen?.dispatch(Forward(counterFeatureFacade.api.createScreen()))
    }

}
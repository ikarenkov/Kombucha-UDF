package com.github.ikarenkov.kombucha.sample.deps

import com.github.ikarenkov.kombucha.sample.NavigationHolder
import com.github.terrakok.modo.stack.Forward
import com.github.ikarenkov.kombucha.sample.counter.api.CounterDeps
import com.github.ikarenkov.kombucha.sample.counter.api.counterFeatureFacade

internal class CounterDepsImpl(
    val navigationHolder: NavigationHolder
) : CounterDeps {

    override fun openNewScreen() {
        navigationHolder.rootScreen?.screen?.dispatch(Forward(counterFeatureFacade.api.createScreen()))
    }

}
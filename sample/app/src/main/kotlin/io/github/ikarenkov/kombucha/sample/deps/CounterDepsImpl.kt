package io.github.ikarenkov.kombucha.sample.deps

import io.github.ikarenkov.kombucha.sample.NavigationHolder
import com.github.terrakok.modo.stack.Forward
import io.github.ikarenkov.kombucha.sample.counter.api.CounterDeps
import io.github.ikarenkov.kombucha.sample.counter.api.counterFeatureFacade

internal class CounterDepsImpl(
    val navigationHolder: NavigationHolder
) : CounterDeps {

    override fun openNewScreen() {
        navigationHolder.rootScreen?.screen?.dispatch(Forward(counterFeatureFacade.api.createScreen()))
    }

}
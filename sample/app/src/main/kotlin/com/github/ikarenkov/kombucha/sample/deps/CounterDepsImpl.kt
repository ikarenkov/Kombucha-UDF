package com.github.ikarenkov.kombucha.sample.deps

import android.content.Intent
import com.github.ikarenkov.kombucha.sample.NavigationHolder
import com.github.terrakok.modo.stack.Forward
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.ikarenkov.teamaker.sample.counter.api.CounterDeps
import ru.ikarenkov.teamaker.sample.counter.api.counterFeatureFacade

internal class CounterDepsImpl(
    val navigationHolder: NavigationHolder
) : CounterDeps {

    override fun openNewScreen() {
        navigationHolder.rootScreen?.screen?.dispatch(Forward(counterFeatureFacade.api.createScreen()))
    }

}
package ru.ikarenkov.teamaker.sample.counter.impl

import ru.ikarenkov.teamaker.CoroutineEffectHandler
import ru.ikarenkov.teamaker.sample.counter.api.CounterDeps

internal class CounterEffectHandler(
    private val deps: CounterDeps
) : CoroutineEffectHandler<Eff.Ext, Nothing>() {

    override fun handleEffect(eff: Eff.Ext) {
        deps.openNewScreen()
    }

}
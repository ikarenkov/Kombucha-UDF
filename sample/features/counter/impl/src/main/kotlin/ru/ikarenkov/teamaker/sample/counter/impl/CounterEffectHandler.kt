package ru.ikarenkov.teamaker.sample.counter.impl

import ru.ikarenkov.teamaker.eff_handler_legacy.CoroutineEffectHandler
import ru.ikarenkov.teamaker.eff_handler_legacy.effectHandler
import ru.ikarenkov.teamaker.sample.counter.api.CounterDeps

internal class CounterEffectHandler(
    private val deps: CounterDeps
) : CoroutineEffectHandler<Eff.Ext, Nothing>() {

    override suspend fun suspendHandleEff(eff: Eff.Ext) {
        deps.openNewScreen()
    }

}

internal fun counterEffectHandler(deps: CounterDeps) = effectHandler<Eff.Ext, Nothing> { _, _ ->
    deps.openNewScreen()
}
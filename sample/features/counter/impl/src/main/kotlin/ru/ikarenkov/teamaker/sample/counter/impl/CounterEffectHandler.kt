package ru.ikarenkov.teamaker.sample.counter.impl

import ru.ikarenkov.teamaker.Cancelable
import ru.ikarenkov.teamaker.eff_handler.EffectHandler
import ru.ikarenkov.teamaker.eff_handler_legacy.effectHandler
import ru.ikarenkov.teamaker.sample.counter.api.CounterDeps

internal class CounterEffectHandler(
    private val deps: CounterDeps
) : EffectHandler<Eff.Ext, Any> {


    override fun handleEff(eff: Eff.Ext, emmit: (Any) -> Unit, emmitTerminate: (Any) -> Unit): Cancelable {
        deps.openNewScreen()
        emmitTerminate(Any())
        return Cancelable { }
    }

}

internal fun counterEffectHandler(deps: CounterDeps) = effectHandler<Eff.Ext, Nothing> { _, _ ->
    deps.openNewScreen()
}
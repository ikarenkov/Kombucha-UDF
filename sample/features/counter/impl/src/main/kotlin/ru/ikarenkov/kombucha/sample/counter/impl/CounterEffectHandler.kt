package ru.ikarenkov.kombucha.sample.counter.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.ikarenkov.kombucha.Cancelable
import ru.ikarenkov.kombucha.eff_handler.EffectHandler
import ru.ikarenkov.kombucha.eff_handler.FlowEffectHandler
import ru.ikarenkov.kombucha.eff_handler_legacy.effectHandler
import ru.ikarenkov.kombucha.sample.counter.api.CounterDeps

internal class CounterEffectHandler(
    private val deps: CounterDeps
) : EffectHandler<Eff.Ext, Any> {


    override fun handleEff(eff: Eff.Ext, emmit: (Any) -> Unit, emmitTerminate: (Any) -> Unit): Cancelable {
        deps.openNewScreen()
        emmitTerminate(Any())
        return Cancelable { }
    }

}

internal class CounterFlowEffectHandler(
    private val deps: CounterDeps
) : FlowEffectHandler<Eff.Ext, Any> {

    override fun handleEff(eff: Eff.Ext): Flow<Any> = flow {
        deps.openNewScreen()
    }

}

internal fun counterEffectHandler(deps: CounterDeps) = effectHandler<Eff.Ext, Nothing> { _, _ ->
    deps.openNewScreen()
}
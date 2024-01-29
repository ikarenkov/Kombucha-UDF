package io.github.ikarenkov.kombucha.sample.counter.impl

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.sample.counter.api.CounterDeps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class CounterEffectHandler(
    private val deps: CounterDeps
) : EffectHandler<CounterFeature.Eff.Ext, Any> {

    override fun handleEff(eff: CounterFeature.Eff.Ext): Flow<Any> = flow {
        when (eff) {
            CounterFeature.Eff.Ext.OpenScreen -> deps.openNewScreen()
        }
    }

}
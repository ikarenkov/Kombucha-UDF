package io.github.ikarenkov.kombucha.sample.counter.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.sample.counter.api.CounterDeps

internal class CounterEffectHandler(
    private val deps: CounterDeps
) : EffectHandler<CounterFeature.Eff.Ext, Any> {

    override fun handleEff(eff: CounterFeature.Eff.Ext): Flow<Any> = flow {
        deps.openNewScreen()
    }

}
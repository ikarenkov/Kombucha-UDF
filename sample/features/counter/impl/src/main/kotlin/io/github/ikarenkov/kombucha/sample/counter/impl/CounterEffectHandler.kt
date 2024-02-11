package io.github.ikarenkov.kombucha.sample.counter.impl

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.sample.counter.api.CounterDeps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

internal class CounterEffectHandler(
    private val deps: CounterDeps
) : EffectHandler<CounterFeature.Eff, CounterFeature.Msg> {

    override fun handleEff(eff: CounterFeature.Eff): Flow<CounterFeature.Msg> = flow {
        when (eff) {
            CounterFeature.Eff.OpenScreen -> deps.openNewScreen()
            CounterFeature.Eff.GenerateRandom -> emit(CounterFeature.Msg.RandomResult(Random.nextInt()))
        }
    }

}

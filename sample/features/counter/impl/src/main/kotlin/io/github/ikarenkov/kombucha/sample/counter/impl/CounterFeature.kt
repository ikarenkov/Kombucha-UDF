package io.github.ikarenkov.kombucha.sample.counter.impl

import android.os.Parcelable
import io.github.ikarenkov.kombucha.reducer.Reducer
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.reducer.toEff
import io.github.ikarenkov.kombucha.reducer.withoutEff
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature.Eff
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature.Msg
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature.State
import io.github.ikarenkov.kombucha.store.ReducerStoreFactory
import io.github.ikarenkov.kombucha.store.Store
import kotlinx.parcelize.Parcelize

internal class CounterStore(
    initialState: State,
    reducerStoreFactory: ReducerStoreFactory,
    counterEffectHandler: CounterEffectHandler
) : Store<Msg, State, Eff> by reducerStoreFactory.create(
    name = "Counter",
    initialState = initialState,
    reducer = CounterFeature.dslReducer,
    effectHandlers = arrayOf(counterEffectHandler)
)

internal object CounterFeature {

    @Parcelize
    internal data class State(
        val counter: Int
    ) : Parcelable

    internal sealed interface Msg {

        data object OnIncreaseClick : Msg
        data object OnDecreaseClick : Msg
        data object OnRandomClick : Msg
        data object OpenScreenClick : Msg
        data class RandomResult(val value: Int) : Msg

    }

    internal sealed interface Eff {

        data object OpenScreen : Eff
        data object GenerateRandom : Eff

    }

    internal val reducerWithoutAdditionalApi: Reducer<Msg, State, Eff> = Reducer<Msg, State, Eff> { msg, state ->
        when (msg) {
            Msg.OnDecreaseClick -> State(state.counter - 1) to emptySet()
            Msg.OnIncreaseClick -> State(state.counter + 1) to emptySet()
            Msg.OpenScreenClick -> state to setOf(Eff.OpenScreen)
            Msg.OnRandomClick -> state to setOf(Eff.GenerateRandom)
            is Msg.RandomResult -> State(counter = msg.value) to emptySet()
        }
    }

    internal val reducerWithAdditionalApi: Reducer<Msg, State, Eff> = Reducer<Msg, State, Eff> { msg, state ->
        when (msg) {
            Msg.OnDecreaseClick -> State(state.counter - 1).withoutEff()
            Msg.OnIncreaseClick -> State(state.counter + 1).withoutEff()
            Msg.OpenScreenClick -> state toEff setOf(Eff.OpenScreen)
            Msg.OnRandomClick -> state toEff Eff.GenerateRandom
            is Msg.RandomResult -> State(counter = msg.value).withoutEff()
        }
    }

    internal val dslReducer: Reducer<Msg, State, Eff> = dslReducer { msg ->
        when (msg) {
            Msg.OnIncreaseClick -> state { copy(counter = counter + 1) }
            Msg.OnDecreaseClick -> state { copy(counter = counter - 1) }
            Msg.OpenScreenClick -> eff(Eff.OpenScreen)
            Msg.OnRandomClick -> eff(Eff.GenerateRandom)
            is Msg.RandomResult -> state { State(counter = msg.value) }
        }
    }

}
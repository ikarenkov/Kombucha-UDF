package io.github.ikarenkov.kombucha.sample.counter.impl

import android.os.Parcelable
import io.github.ikarenkov.kombucha.eff_handler.adaptCast
import io.github.ikarenkov.kombucha.reducer.Reducer
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.reducer.toEff
import io.github.ikarenkov.kombucha.reducer.withoutEff
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature.Eff
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature.Msg
import io.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature.State
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreFactory
import kotlinx.parcelize.Parcelize

internal class CounterFeature(
    initialState: State,
    storeFactory: StoreFactory,
    counterEffectHandler: CounterEffectHandler
) : Store<Msg, State, Eff> by storeFactory.create(
    name = "Counter",
    initialState = initialState,
    reducer = counterDslReducerReducer,
    effectHandlers = arrayOf(counterEffectHandler.adaptCast())
) {

    @Parcelize
    internal data class State(
        val counter: Int
    ) : Parcelable

    internal sealed interface Msg {

        sealed interface Ui : Msg {

            data object OnIncreaseClick : Ui
            data object OnDecreaseClick : Ui
            data object OpenScreenClick : Ui

        }

    }

    internal sealed interface Eff {

        sealed interface Ext : Eff {

            data object OpenScreen : Ext

        }

    }

}

internal val counterReducerNoAdditionalApi: Reducer<Msg, State, Eff> = Reducer<Msg, State, Eff> { msg, state ->
    when (msg) {
        Msg.Ui.OnDecreaseClick -> State(state.counter - 1) to emptySet()
        Msg.Ui.OnIncreaseClick -> State(state.counter + 1) to emptySet()
        Msg.Ui.OpenScreenClick -> state to setOf(Eff.Ext.OpenScreen)
    }
}

internal val counterReducerWithAdditionalApi: Reducer<Msg, State, Eff> = Reducer<Msg, State, Eff> { msg, state ->
    when (msg) {
        Msg.Ui.OnDecreaseClick -> State(state.counter - 1).withoutEff()
        Msg.Ui.OnIncreaseClick -> State(state.counter + 1).withoutEff()
        Msg.Ui.OpenScreenClick -> state toEff setOf(Eff.Ext.OpenScreen)
    }
}

internal val counterDslReducerReducer: Reducer<Msg, State, Eff> = dslReducer { msg ->
    when (msg) {
        Msg.Ui.OnIncreaseClick -> state { copy(counter = counter + 1) }
        Msg.Ui.OnDecreaseClick -> state { copy(counter = counter - 1) }
        Msg.Ui.OpenScreenClick -> eff(Eff.Ext.OpenScreen)
    }
}
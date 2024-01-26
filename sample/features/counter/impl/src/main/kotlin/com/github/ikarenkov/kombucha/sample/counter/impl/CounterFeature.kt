package com.github.ikarenkov.kombucha.sample.counter.impl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.github.ikarenkov.kombucha.eff_handler.adaptCast
import com.github.ikarenkov.kombucha.reducer.Reducer
import com.github.ikarenkov.kombucha.reducer.dslReducer
import com.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature.Eff
import com.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature.Msg
import com.github.ikarenkov.kombucha.sample.counter.impl.CounterFeature.State
import com.github.ikarenkov.kombucha.store.Store
import com.github.ikarenkov.kombucha.store.StoreFactory

internal class CounterFeature(
    initialState: State,
    storeFactory: StoreFactory,
    counterEffectHandler: CounterEffectHandler
) : Store<Msg, State, Eff> by storeFactory.create(
    name = "Counter",
    initialState = initialState,
    reducer = CounterReducer,
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

    object CounterReducer : Reducer<Msg, State, Eff> by dslReducer({ msg ->
        when (msg) {
            Msg.Ui.OnIncreaseClick -> state { copy(counter = counter + 1) }
            Msg.Ui.OnDecreaseClick -> state { copy(counter = counter - 1) }
            Msg.Ui.OpenScreenClick -> eff(Eff.Ext.OpenScreen)
        }
    })
}
package ru.ikarenkov.teamaker.sample.counter.impl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.ikarenkov.teamaker.reducer.dslReducer

internal val rootReducer = dslReducer<Msg, State, Eff> { msg ->
    when (msg) {
        Msg.Ui.OnIncreaseClick -> state { copy(counter = counter + 1) }
        Msg.Ui.OnDecreaseClick -> state { copy(counter = counter - 1) }
        Msg.Ui.OpenScreenClick -> eff(Eff.Ext.OpenScreen)
    }
}

@Parcelize
internal data class State(
    val counter: Int
) : Parcelable

internal sealed interface Msg {

    sealed interface Ui : Msg {

        object OnIncreaseClick : Ui
        object OnDecreaseClick : Ui
        object OpenScreenClick : Ui

    }

}

internal sealed interface Eff {

    sealed interface Ext : Eff {

        object OpenScreen : Ext

    }

}
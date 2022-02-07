package ru.ikarenkov.teamaker.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.ikarenkov.teamaker.Store

interface TeaScreen<UiMsg : Any, Model : Any, UiEff : Any> {

    fun dispatch(msg: UiMsg)

    val state: Model

}

fun <Msg : Any, UiMsg : Msg, Model : Any, Eff : Any, UiEff : Any> brewComposeTea(tea: Store<Msg, Model, Eff>) =
    object : TeaScreen<UiMsg, Model, UiEff> {

        override var state: Model by mutableStateOf(tea.currentState)

        init {
            tea.listenState { state = it }
        }

        override fun dispatch(msg: UiMsg) {
            tea.accept(msg)
        }

    }
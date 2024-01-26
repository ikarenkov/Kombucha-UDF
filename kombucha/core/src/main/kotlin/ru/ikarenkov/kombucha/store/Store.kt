package ru.ikarenkov.kombucha.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.ikarenkov.kombucha.Cancelable

interface Store<Msg : Any, State : Any, Eff : Any> : Cancelable {

    val state: StateFlow<State>

    val effects: Flow<Eff>

    fun accept(msg: Msg)

}

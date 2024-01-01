package ru.ikarenkov.teamaker.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.ikarenkov.teamaker.Cancelable

interface Store<Msg : Any, Model : Any, Eff : Any> : Cancelable {

    val state: StateFlow<Model>

    val effects: Flow<Eff>

    fun dispatch(msg: Msg)

}

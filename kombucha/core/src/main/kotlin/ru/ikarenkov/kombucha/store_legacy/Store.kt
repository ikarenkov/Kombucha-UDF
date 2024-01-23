package ru.ikarenkov.kombucha.store_legacy

interface Store<Msg : Any, Model : Any, Eff : Any> : Cancelable {

    val currentState: Model

    fun dispatch(msg: Msg)

    fun listenState(listener: (model: Model) -> Unit): Cancelable

    fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable

}

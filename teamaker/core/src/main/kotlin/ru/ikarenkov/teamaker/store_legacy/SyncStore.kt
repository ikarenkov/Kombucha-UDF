package ru.ikarenkov.teamaker.store_legacy

import ru.ikarenkov.teamaker.Cancelable
import ru.ikarenkov.teamaker.addListenerAndMakeCancelable
import ru.ikarenkov.teamaker.eff_handler_legacy.EffectHandler
import ru.ikarenkov.teamaker.notifyAll

class SyncStore<Msg : Any, Model : Any, Eff : Any>(
    initialState: Model,
    private val reducer: (Model, Msg) -> Pair<Model, Set<Eff>>,
    private val effHandlers: List<EffectHandler<Eff, Msg>> = listOf(),
    initialEffects: Set<Eff> = setOf(),
) : Store<Msg, Model, Eff> {

    override var currentState: Model = initialState
        private set

    private var isCanceled = false
    private val stateListeners = mutableListOf<(state: Model) -> Unit>()
    private val effListeners = mutableListOf<(eff: Eff) -> Unit>()

    init {
        initEffHandlers(initialEffects)
    }

    override fun dispatch(msg: Msg) {
        if (isCanceled) {
            return
        }
        val (newState, commands) = reducer(currentState, msg)
        currentState = newState
        stateListeners.notifyAll(newState)
        commands.forEach { command ->
            effListeners.notifyAll(command)
        }
    }

    override fun listenState(listener: (state: Model) -> Unit): Cancelable {
        val cancelable = stateListeners.addListenerAndMakeCancelable(listener)
        listener(currentState)
        return cancelable
    }

    override fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable =
        effListeners.addListenerAndMakeCancelable(listener)

    override fun cancel() {
        isCanceled = true
        effHandlers.forEach(EffectHandler<Eff, Msg>::cancel)
    }

    private fun initEffHandlers(initialEffects: Set<Eff>) {
        effHandlers.forEach {
            it.setListener(::dispatch)
            listenEffect(it::handleEff)
            initialEffects.forEach(it::handleEff)
        }
    }

}

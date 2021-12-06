package com.ikarenkov.teamaker

/**
 * This interface must only be used in legacy features, when we want to use effect handlers, but don't want to refactor state and add reducers.
 */
interface LegacyEffectHandlerFeature<Msg : Any, Eff : Any> : Cancelable {

    fun accept(msg: Msg)

    fun sendEff(eff: Eff)

    fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable

}

class LegacyEffectHandlerFeatureImpl<Msg : Any, Eff : Any>(
        private val reducer: (Msg) -> Set<Eff>
) : LegacyEffectHandlerFeature<Msg, Eff> {

    private var isCanceled = false
    private val effListeners = mutableListOf<(eff: Eff) -> Unit>()

    override fun accept(msg: Msg) {
        if (isCanceled) {
            return
        }
        val commands = reducer(msg)
        commands.forEach { command ->
            effListeners.notifyAll(command)
        }
    }

    override fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable =
            effListeners.addListenerAndMakeCancelable(listener)

    override fun cancel() {
        isCanceled = true
    }

    override fun sendEff(eff: Eff) {
        if (isCanceled) {
            return
        }
        effListeners.notifyAll(eff)
    }

}

fun <Msg : Any, Eff : Any> LegacyEffectHandlerFeature<Msg, Eff>.wrapWithEffectHandler(
        effectHandler: EffectHandler<Eff, Msg>,
        initialEffects: Set<Eff> = emptySet()
) = object : LegacyEffectHandlerFeature<Msg, Eff> by this {
    override fun cancel() {
        effectHandler.cancel()
        this@wrapWithEffectHandler.cancel()
    }
}.apply {
    effectHandler.setListener { msg -> accept(msg) }
    listenEffect { eff ->
        effectHandler.handleEffect(eff)
    }
    initialEffects.forEach(effectHandler::handleEffect)
}
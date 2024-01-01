package ru.ikarenkov.teamaker.eff_handler

import ru.ikarenkov.teamaker.Cancelable

interface EffectHandler<Eff : Any, Msg : Any> {

    fun handleEff(eff: Eff, emmit: (Msg) -> Unit, emmitTerminate: (Msg) -> Unit): Cancelable

}

inline fun <reified Eff1 : Any, Msg1 : Any, Eff2 : Any, reified Msg2 : Any> EffectHandler<Eff1, Msg1>.adaptCast(): EffectHandler<Eff2, Msg2> = adapt(
    effAdapter = { it as? Eff1 },
    msgAdapter = { it as? Msg2 }
)

fun <Eff1 : Any, Msg1 : Any, Eff2 : Any, Msg2 : Any> EffectHandler<Eff1, Msg1>.adapt(
    effAdapter: (Eff2) -> Eff1?,
    msgAdapter: (Msg1) -> Msg2? = { null }
): EffectHandler<Eff2, Msg2> = object : EffectHandler<Eff2, Msg2> {

    override fun handleEff(eff: Eff2, emmit: (Msg2) -> Unit, emmitTerminate: (Msg2) -> Unit) =
        effAdapter(eff)
            ?.let {
                handleEff(
                    eff = it,
                    emmit = { msg -> msgAdapter(msg)?.let(emmit) },
                    emmitTerminate = { msg -> msgAdapter(msg)?.let(emmitTerminate) },
                )
            }
            ?: Cancelable {}

}

//fun <Msg : Any, State : Any, Eff : Any> Store<Msg, State, Eff>.wrapWithEffectHandler(
//    effectHandler: EffectHandler<Eff, Msg>,
//    initialEffects: Set<Eff> = emptySet()
//) = object : Store<Msg, State, Eff> by this {
//    override fun cancel() {
//        effectHandler.cancel()
//        this@wrapWithEffectHandler.cancel()
//    }
//}.apply {
//    effectHandler.setListener(::dispatch)
//    listenEffect(effectHandler::handleEff)
//    initialEffects.forEach(effectHandler::handleEff)
//}
//
//fun <Msg : Any, State : Any, Eff : Any> Store<Msg, State, Eff>.wrapWithEffectHandlers(
//    vararg effectHandlers: EffectHandler<Eff, Msg>,
//    initialEffects: Set<Eff> = emptySet()
//) = object : Store<Msg, State, Eff> by this {
//    override fun cancel() {
//        effectHandlers.forEach { it.cancel() }
//        this@wrapWithEffectHandlers.cancel()
//    }
//}.apply {
//    effectHandlers.forEach {
//        it.setListener(::dispatch)
//        listenEffect(it::handleEff)
//        initialEffects.forEach(it::handleEff)
//    }
//}
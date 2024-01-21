package ru.ikarenkov.teamaker.eff_handler_legacy

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

abstract class CoroutineEffectHandler<Eff : Any, Msg : Any>(
    executorCoroutineDispatcher: CoroutineDispatcher = Dispatchers.Main
) : EffectHandler<Eff, Msg>, CoroutineScope {

    final override val coroutineContext: CoroutineContext by lazy {
        SupervisorJob() + executorCoroutineDispatcher
    }

    private var listener: ((Msg) -> Unit)? = null

    final override fun setListener(listener: (Msg) -> Unit) {
        this.listener = listener
    }

    final override fun cancel() {
        (this as CoroutineScope).cancel()
    }

    protected suspend fun sendMsgToMain(msg: Msg) {
        withContext(Dispatchers.Main) { sendMsg(msg) }
    }

    protected fun sendMsg(msg: Msg) {
        listener?.invoke(msg)
    }

    final override fun handleEff(eff: Eff) {
        launch { suspendHandleEff(eff) }
    }

    abstract suspend fun suspendHandleEff(eff: Eff)

}

fun <Eff : Any, Msg : Any> effectHandler(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Main,
    handleEffect: suspend (eff: Eff, dispatcher: suspend (Msg) -> Unit) -> Any?
) = object : CoroutineEffectHandler<Eff, Msg>(coroutineDispatcher) {

    override suspend fun suspendHandleEff(eff: Eff) {
        handleEffect(eff) {
            withContext(Dispatchers.Main) { sendMsg(it) }
        }
    }

}

fun <Eff : Any, Msg : Any> ioEffectHandler(
    handleEffect: suspend (eff: Eff, dispatcher: suspend (Msg) -> Unit) -> Any?
) = effectHandler(Dispatchers.IO, handleEffect)
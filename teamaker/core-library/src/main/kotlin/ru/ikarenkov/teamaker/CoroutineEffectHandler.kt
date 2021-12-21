package ru.ikarenkov.teamaker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

abstract class CoroutineEffectHandler<Eff : Any, Msg : Any>(
    executorCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
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

}

fun <Eff : Any, Msg : Any> effectHandler(
    handleEffect: suspend (eff: Eff, dispatcher: suspend (Msg) -> Unit) -> Any?
) = object : CoroutineEffectHandler<Eff, Msg>() {

    override fun handleEffect(eff: Eff) {
        launch {
            handleEffect(eff) {
                withContext(Dispatchers.Main) { sendMsg(it) }
            }
        }
    }

}
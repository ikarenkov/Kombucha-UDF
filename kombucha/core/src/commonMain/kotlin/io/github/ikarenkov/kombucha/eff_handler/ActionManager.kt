package io.github.ikarenkov.kombucha.eff_handler

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Allows to execute requests for [EffectHandler] implementations in a switching manner. Each request
 * will cancel the previous one.
 *
 * Example:
 * ```
 * private val actionManager = ActionManager()
 *
 *
 * override fun handleEff(eff: Eff.Int): Flow<Msg> = when (eff) {
 *      is Eff.MyEff -> actionManager.recreateAction {
 *          flow { ... }
 *      }
 * }
 * ```
 */
class ActionManager {

    private var currentChannel: SendChannel<*>? = null
    private val lock = Mutex()

    /**
     * Collect given flow as a job and cancels all previous ones.
     *
     * @param delayMillis operation delay measured with milliseconds. Can be specified to debounce
     * existing requests.
     * @param action actual event source
     */
    fun <Event : Any> recreateAction(
        delayMillis: Long = 0,
        action: () -> Flow<Event>,
    ): Flow<Event> {
        return callbackFlow {
            lock.withLock {
                currentChannel?.close()
                currentChannel = channel
            }

            delay(delayMillis)

            action.invoke()
                .onEach { send(it) }
                .catch { close(it) }
                .collect()

            channel.close()
        }
    }

    fun <Event : Any> cancelAction(
        delayMillis: Long = 0,
    ): Flow<Event> = flow {
        delay(delayMillis)
        lock.withLock {
            currentChannel?.close()
            currentChannel = null
        }
    }
}

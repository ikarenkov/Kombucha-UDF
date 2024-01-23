package ru.ikarenkov.kombucha.store_legacy

internal fun <T> List<(T) -> Unit>.notifyAll(msg: T) = forEach { listener -> listener.invoke(msg) }

internal fun <T> MutableList<(T) -> Unit>.addListenerAndMakeCancelable(listener: (T) -> Unit): Cancelable {
    add(listener)
    return object : Cancelable {
        override fun cancel() {
            remove(listener)
        }
    }
}

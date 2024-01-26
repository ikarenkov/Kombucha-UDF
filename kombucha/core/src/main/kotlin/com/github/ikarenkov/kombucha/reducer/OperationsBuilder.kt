package com.github.ikarenkov.kombucha.reducer

@DslMarker
internal annotation class OperationsBuilderDsl

@OperationsBuilderDsl
class OperationsBuilder<T : Any> {

    private val list = mutableListOf<T>()

    /**
     * Schedules effect to be sent as result of reducer
     */
    operator fun T?.unaryPlus() {
        this?.let(list::add)
    }

    internal fun build(): List<T> = list

}
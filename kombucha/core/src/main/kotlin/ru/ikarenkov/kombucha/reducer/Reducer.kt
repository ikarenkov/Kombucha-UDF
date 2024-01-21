package ru.ikarenkov.kombucha.reducer

/**
 * Base fun interface for reducer which allows to add types for State, Msg and Eff and autogenerate invoke method with given param.
 */
fun interface Reducer<Msg : Any, State : Any, Eff : Any> {

    operator fun invoke(state: State, msg: Msg): Pair<State, Set<Eff>>

}

infix fun <T, E> T.toEff(eff: E) = this to setOf(eff)

infix fun <T, E> T.toEff(eff: Set<E>) = this to eff
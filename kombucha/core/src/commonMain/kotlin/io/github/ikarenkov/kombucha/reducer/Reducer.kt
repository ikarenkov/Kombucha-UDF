package io.github.ikarenkov.kombucha.reducer

/**
 * Base fun interface for reducer which allows to add types for State, Msg and Eff and autogenerate invoke method with given param.
 */
fun interface Reducer<Msg : Any, State : Any, Eff : Any> {

    operator fun invoke(msg: Msg, state: State): ReducerResult<State, Eff>

}

typealias ReducerResult<State, Eff> = Pair<State, Set<Eff>>

infix fun <T, E> T.toEff(eff: E) = this to setOf(eff)

infix fun <T, E> T.toEff(eff: Set<E>) = this to eff

fun <T, E> T.withoutEff() = this to emptySet<E>()

package io.github.ikarenkov.kombucha.test

import io.github.ikarenkov.kombucha.reducer.ReducerResult

data class ReducerTestData<Msg : Any, State : Any, Eff : Any>(
    val msg: Msg,
    val expectedState: State,
    val expectedEffects: Set<Eff> = emptySet()
) {
    constructor(msg: Msg, reducerResult: ReducerResult<State, Eff>) : this(msg, reducerResult.first, reducerResult.second)
}

/**
 * Convenient api for creating [ReducerResult], use it with [returns].
 */
infix operator fun <State : Any, Eff : Any> State.plus(eff: Eff): ReducerResult<State, Eff> =
    ReducerResult(this, setOf(eff))

/**
 * Convenient api for creating [ReducerResult], use it with [returns].
 */
infix operator fun <State : Any, Eff : Any> State.plus(effects: Set<Eff>): ReducerResult<State, Eff> =
    ReducerResult(this, effects)

/**
 * Convenient api for creating [ReducerTestData].
 */
infix fun <Msg : Any, State : Any, Eff : Any> Msg.returns(resultProvider: ReducerResult<State, Eff>) =
    ReducerTestData(this, resultProvider.first, resultProvider.second)

/**
 * Convenient api for creating [ReducerTestData]
 */
infix fun <Msg : Any, State : Any, Eff : Any> Msg.returns(state: State) =
    ReducerTestData(this, state, emptySet<Eff>())
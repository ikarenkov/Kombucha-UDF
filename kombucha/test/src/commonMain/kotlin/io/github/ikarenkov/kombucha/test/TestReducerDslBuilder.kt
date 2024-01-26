package io.github.ikarenkov.kombucha.test

import io.github.ikarenkov.kombucha.reducer.ReducerResult

data class ReducerTestData<Msg : Any, State : Any, Eff : Any>(
    val msg: Msg,
    val providedState: State,
    val providedEffects: Set<Eff> = emptySet()
) {
    constructor(msg: Msg, reducerResult: ReducerResult<State, Eff>) : this(msg, reducerResult.first, reducerResult.second)
}

class TestReducerDslBuilder<Msg : Any, State : Any, Eff : Any> {

    private val result = mutableListOf<ReducerTestData<Msg, State, Eff>>()
    infix fun Msg.returns(resultProvider: ReducerResult<State, Eff>) {
        result += ReducerTestData(this, resultProvider.first, resultProvider.second)
    }

    infix fun Msg.returns(state: State) {
        result += ReducerTestData(this, state, emptySet())
    }

    infix operator fun State.plus(eff: Eff): ReducerResult<State, Eff> =
        ReducerResult(this, setOf(eff))

    infix operator fun State.plus(effects: Set<Eff>): ReducerResult<State, Eff> =
        ReducerResult(this, effects)

    infix operator fun ReducerResult<State, Eff>.plus(eff: Eff): ReducerResult<State, Eff> =
        copy(second = second.plusElement(eff))

    infix operator fun ReducerResult<State, Eff>.plus(effects: Set<Eff>): ReducerResult<State, Eff> =
        copy(second = second + effects)

    fun build() = result

}
package io.github.ikarenkov.kombucha.test

import io.github.ikarenkov.kombucha.reducer.Reducer
import io.github.ikarenkov.kombucha.reducer.ReducerResult
import kotlin.jvm.JvmName

/**
 * DSL used for testing reducer behavior. It emulate work of the store and allows to assert [reducer] behavior using [assertReturn] fun.
 */
class TestReducerDsl<Msg : Any, State : Any, Eff : Any>(
    initialState: State,
    private val reducer: Reducer<Msg, State, Eff>
) {

    private var currentState: State = initialState
    private var step: Int = 0

    infix fun Msg.assertReturn(resultProvider: ReducerResult<State, Eff>) {
        assertReducerRunCorrect(ReducerTestData(this, resultProvider.first, resultProvider.second))
    }

    infix fun Msg.assertReturn(state: State) {
        assertReducerRunCorrect(ReducerTestData(this, state, emptySet()))
    }

    @JvmName("returnsReducerResultBuilder")
    infix fun Msg.assertReturn(reducerResultBuilder: () -> ReducerResult<State, Eff>) {
        val reducerResult = reducerResultBuilder()
        assertReducerRunCorrect(ReducerTestData(this, reducerResult.first, reducerResult.second))
    }

    infix operator fun ReducerResult<State, Eff>.plus(eff: Eff): ReducerResult<State, Eff> =
        copy(second = second.plusElement(eff))

    infix operator fun ReducerResult<State, Eff>.plus(effects: Set<Eff>): ReducerResult<State, Eff> =
        copy(second = second.toMutableSet().apply { addAll(effects) })

    /**
     * Convenient api for creating [ReducerResult], use it with [assertReturn].
     */
    infix operator fun <State : Any, Eff : Any> State.plus(eff: Eff): ReducerResult<State, Eff> =
        ReducerResult(this, setOf(eff))

    /**
     * Convenient api for creating [ReducerResult], use it with [assertReturn].
     */
    infix operator fun <State : Any, Eff : Any> State.plus(effects: Set<Eff>): ReducerResult<State, Eff> =
        ReducerResult(this, effects)

    private fun assertReducerRunCorrect(reducerTestData: ReducerTestData<Msg, State, Eff>) {
        assertReducerRunCorrect(
            testStep = step,
            currentState = currentState,
            reducerTestData = reducerTestData,
            reducer = reducer
        )
        currentState = reducerTestData.expectedState
    }

}
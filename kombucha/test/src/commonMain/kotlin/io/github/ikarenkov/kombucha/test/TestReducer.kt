package io.github.ikarenkov.kombucha.test

import io.github.ikarenkov.kombucha.reducer.Reducer
import kotlin.test.assertEquals

/**
 * Test given [reducer] by passing [initialState] and given messages described in [reducerResultsBuilder].
 * It is made by consequential passing msg and new provided states, starting with [initialState].

 * Sample:
 *
 * ```kotlin
 * testReducer(
 *     initialState = State.Init(false),
 *     reducer = AuthFeature.Reducer
 * ) {
 *     Msg.Init returns State.Init(inProgress = true) + Eff.LoadCachedData
 *     Msg.LoadCacheAuthResult(null) returns State.NotAuthorized.Idle
 * }
 * ```
 * @param initialState - the state from which we start computations.
 * @param reducer - reducer that behavior we want to check.
 * @param reducerResultsBuilder - DSL description of expected behavior, consisting of given msg and expected result, returning by this msg.
 */
fun <Msg : Any, State : Any, Eff : Any> testReducer(
    initialState: State,
    reducer: Reducer<Msg, State, Eff>,
    reducerResultsBuilder: TestReducerDsl<Msg, State, Eff>.() -> Unit
) {
    TestReducerDsl<Msg, State, Eff>(initialState, reducer).apply(reducerResultsBuilder)
}

/**
 * Test given [reducer] by passing [initialState] and given messages from [expectedReducerResults].
 * It is made by consequential passing msg and new provided states, starting with [initialState]
 *
 * Sample:
 *
 * ```kotlin
 * testReducer(
 *     initialState = State.Init(false),
 *     reducer = AuthFeature.Reducer,
 *     expectedReducerResults = listOf(
 *         ReducerTestData(Msg.Init, State.Init(inProgress = true), setOf(Eff.LoadCachedData)),
 *         ReducerTestData(Msg.LoadCacheAuthResult(null), State.NotAuthorized.Idle)
 *     )
 * )
 * ```
 * @param initialState - the state from which we start computations.
 * @param reducer - reducer that behavior we want to check.
 * @param expectedReducerResults - description of expected behavior, consisting of given msg and expected result, returning by this msg.
 */
fun <Msg : Any, State : Any, Eff : Any> testReducer(
    initialState: State,
    reducer: Reducer<Msg, State, Eff>,
    expectedReducerResults: List<ReducerTestData<Msg, State, Eff>>
) {
    var previousState: State = initialState
    for ((index, reducerTestData) in expectedReducerResults.withIndex()) {
        assertReducerRunCorrect(
            testStep = index,
            currentState = previousState,
            reducerTestData = reducerTestData,
            reducer = reducer
        )
        // if previous state is not failing then we can take state from test data
        previousState = reducerTestData.expectedState
    }
}

internal fun <Msg : Any, State : Any, Eff : Any> assertReducerRunCorrect(
    testStep: Int,
    currentState: State,
    reducerTestData: ReducerTestData<Msg, State, Eff>,
    reducer: Reducer<Msg, State, Eff>,
) {
    val (msg, expectState, expectEffects) = reducerTestData
    val (actualState, actualEffects) = reducer(msg = msg, state = currentState)
    assertEquals(
        expected = expectState to expectEffects,
        actual = actualState to actualEffects,
        message = getAssertionMessage(testStep, msg, currentState, expectState, expectEffects, actualState, actualEffects)
    )
}

private fun <Eff : Any, Msg : Any, State : Any> getAssertionMessage(
    testStep: Int,
    msg: Msg,
    previousState: State,
    expectState: State,
    expectEffects: Set<Eff>,
    actualState: State,
    actualEffects: Set<Eff>
) = buildString {
    appendLine("Test reducer step $testStep: Expected behavior differs from actual.")
    appendLine("==========================================================")

    appendLine("  Msg  : $msg")
    appendLine("   +")
    appendLine(" State : $previousState")
    appendLine("   |")
    appendLine("   V")
    addExpectedActual("State", expectState, actualState)
    appendLine("   +")
    addExpectedActual("Effects", expectEffects, actualEffects)

    appendLine("==========================================================")
}

private fun <T : Any> StringBuilder.addExpectedActual(name: String, expectState: T, actualState: T) {
    if (expectState == actualState) {
        appendLine(" $name : $actualState")
    } else {
        appendLine(" $name :")
        appendLine("    Expected: $expectState")
        appendLine("    Actual  : $actualState")
    }
}
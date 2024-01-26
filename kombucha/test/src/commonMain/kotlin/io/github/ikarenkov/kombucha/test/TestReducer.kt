package io.github.ikarenkov.kombucha.test

import io.github.ikarenkov.kombucha.reducer.Reducer
import kotlin.test.assertContentEquals

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
    reducerResultsBuilder: TestReducerDslBuilder<Msg, State, Eff>.() -> Unit
) {
    testReducer(initialState, reducer, TestReducerDslBuilder<Msg, State, Eff>().apply(reducerResultsBuilder).build())
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
    val actualReducerResults = mutableListOf<ReducerTestData<Msg, State, Eff>>()
    for ((msg, _, _) in expectedReducerResults) {
        val previousState = actualReducerResults.lastOrNull()?.providedState ?: initialState
        val reducerResult = reducer(msg = msg, state = previousState)
        actualReducerResults += ReducerTestData(msg, reducerResult)
    }
    assertContentEquals(
        expectedReducerResults,
        actualReducerResults
    )
}
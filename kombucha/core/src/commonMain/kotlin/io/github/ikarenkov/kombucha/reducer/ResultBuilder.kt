package io.github.ikarenkov.kombucha.reducer

/**
 * DSL builder for dsl reducer, which allows to write state updating and effect creation in declarative way.
 * Also you can perform only required actions, f.e. only update state or only create effects, without needing to return `Pair<State, Set<Eff>>`.
 *
 * F.e. you can describe updating `State(value: Int)` using:
 * ```kotlin
 * state { copy(value = value + 5) }
 * ```
 * or sending effects
 * ```kotlin
 * eff {
 *     +Eff.LoadDate
 *     +Eff.ShowToast
 * }
 * ```
 */
open class ResultBuilder<State : Any, Eff : Any>(
    /**
     * Initial state for state and state {}. Initial state is val and it is permanent.
     */
    @Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
    val initialState: State
) {

    /**
     * Current built state. It can be updated using `state {}` fun,
     */
    val state: State
        get() = currentState

    private var currentState: State = initialState

    private val effectsList = mutableListOf<Eff>()

    /**
     * Dsl for updating current state.
     * It passes initial state as this for first call, next times it passes last updated state.
     *
     * F.e.
     * ```kotlin
     * state { copy(value = value + 5) }
     * ```
     */
    fun state(update: State.() -> State) {
        currentState = currentState.update()
    }

    /**
     * Dsl for updating effects.
     *
     * F.e.
     * ```kotlin
     * eff {
     *     +Eff.LoadDate
     *     +Eff.ShowToast
     * }
     * ```
     */
    fun eff(vararg effects: Eff) {
        effects.forEach {
            effectsList += it
        }
    }

    @PublishedApi
    internal fun build(): ReducerResult<State, Eff> = currentState to effectsList.toSet()

}
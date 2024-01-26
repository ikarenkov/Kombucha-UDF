package io.github.ikarenkov.kombucha.reducer

/**
 * DSL builder for reducer, which allows to write state updating and effect creation in declarative way.
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
fun <Msg : Any, State : Any, Eff : Any> dslReducer(
    reduce: ResultBuilder<State, Eff>.(msg: Msg) -> Unit
) = Reducer<Msg, State, Eff> { msg, state ->
    ResultBuilder<State, Eff>(state).apply { reduce(msg) }.build()
}

/**
 * DSL builder for reducer, like [dslReducer] with two reducers for ui and internal messages.
 * It allow to specify types as reified parameters, and auto separate uiMsg and internalMsg.
 */
inline fun <Msg : Any, reified UiMsg : Msg, reified InternalMsg : Msg, State : Any, Eff : Any> screenDslReducer(
    noinline uiReducer: ResultBuilder<State, Eff>.(msg: UiMsg) -> Any?,
    noinline internalReducer: ResultBuilder<State, Eff>.(msg: InternalMsg) -> Any?,
) = Reducer<Msg, State, Eff> { msg, state ->
    when (msg) {
        is UiMsg -> ResultBuilder<State, Eff>(state).apply { uiReducer(msg) }
        is InternalMsg -> ResultBuilder<State, Eff>(state).apply { internalReducer(msg) }
        else -> error("Msg ${msg.javaClass} is neither UI nor Internal")
    }.build()
}
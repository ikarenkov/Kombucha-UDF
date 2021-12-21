package ru.ikarenkov.teamaker.sample.root

import ru.ikarenkov.teamaker.reducer.dslReducer

val rootReducer = dslReducer<Msg, State, Eff> { msg ->
    when (msg) {
        Msg.OnIncreaseClick -> state { copy(counter = counter + 1) }
        Msg.OnDecreaseClick -> state { copy(counter = counter - 1) }
    }
}

data class State(
    val counter: Int
)

sealed interface Msg {
    object OnIncreaseClick : Msg
    object OnDecreaseClick : Msg
}

sealed interface Eff
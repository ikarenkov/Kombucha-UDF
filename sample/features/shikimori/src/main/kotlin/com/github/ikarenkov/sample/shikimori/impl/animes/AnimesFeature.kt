package com.github.ikarenkov.sample.shikimori.impl.animes

import com.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeature.Eff
import com.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeature.Msg
import com.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeature.State
import com.github.ikarenkov.sample.shikimori.impl.storeCoroutineExceptionHandler
import ru.ikarenkov.teamaker.reducer.dslReducer
import ru.ikarenkov.teamaker.store.CoroutinesStore

internal class AnimesFeature : CoroutinesStore<Msg, State, Eff>(
    name = "Animes",
    initialState = State.NotAuthorized,
    reducer = reducer::invoke,
    effHandlers = emptyList(),
    coroutineExceptionHandler = storeCoroutineExceptionHandler("Animes")
) {

    sealed interface Msg {
        data object Authorize : Msg
        sealed interface AuthorizationResult : Msg {
            data class Error(val throwable: Throwable) : AuthorizationResult
            data object Success : AuthorizationResult
        }
    }

    sealed interface State {
        data object Authorized : State
        data object AuthInProgress : State
        data object NotAuthorized : State
    }

    sealed interface Eff {
        data object Authorize : Eff
    }

}

internal val reducer = dslReducer<Msg, State, Eff> { msg ->
    when (msg) {
        is Msg.AuthorizationResult.Error -> state { State.NotAuthorized }
        Msg.AuthorizationResult.Success -> state { State.Authorized }
        Msg.Authorize -> {
            if (state is State.NotAuthorized) {
                state { State.AuthInProgress }
                eff(Eff.Authorize)
            }
        }
    }
}

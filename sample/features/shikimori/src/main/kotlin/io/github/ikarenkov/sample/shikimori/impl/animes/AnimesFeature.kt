package io.github.ikarenkov.sample.shikimori.impl.animes

import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeature.Eff
import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeature.Msg
import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeature.State
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreFactory

internal class AnimesFeature(
    val storeFactory: StoreFactory
) : Store<Msg, State, Eff> by storeFactory.create(
    name = "Animes",
    initialState = State.NotAuthorized,
    reducer = reducer::invoke,
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

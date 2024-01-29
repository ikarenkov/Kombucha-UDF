package io.github.ikarenkov.sample.shikimori.impl.animes

import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreFactory
import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeature.Eff
import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeature.Msg
import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeature.State

internal class AnimesFeature(
    val storeFactory: StoreFactory
) : Store<Msg, State, Eff> by storeFactory.create(
    name = "Animes",
    initialState = State.NotAuthorized,
    reducer = reducer::invoke,
) {

    sealed interface Msg {
        data object OnAuthClick : Msg
        sealed interface AuthorizationResult : Msg {
            data object Authorized : AuthorizationResult
            data object NotAuthorized : AuthorizationResult
            data object Loading : AuthorizationResult
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
        Msg.OnAuthClick -> {
            when (state) {
                is State.NotAuthorized -> eff(Eff.Authorize)
                State.AuthInProgress -> {}
                State.Authorized -> {
                    // todo: logout
                }
            }
        }
        Msg.AuthorizationResult.Authorized -> state { State.Authorized }
        Msg.AuthorizationResult.Loading -> state { State.AuthInProgress }
        Msg.AuthorizationResult.NotAuthorized -> state { State.NotAuthorized }
    }
}

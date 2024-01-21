package com.github.ikarenkov.sample.shikimori.impl.auth

import ru.ikarenkov.teamaker.eff_handler.adaptCast
import ru.ikarenkov.teamaker.store.Store
import ru.ikarenkov.teamaker.store.StoreFactory

internal class AuthFeatureFactory(
    private val storeFactory: StoreFactory,
    private val authEffectHandler: AuthFeature.AuthEffHandler
) {

    fun createAuthFeature(): Store<AuthFeature.Msg, AuthFeature.State, AuthFeature.Eff> =
        storeFactory.create<AuthFeature.Msg, AuthFeature.State, AuthFeature.Eff>(
            name = "AuthFeature",
            initialState = AuthFeature.State.Init(inProgress = false),
            reducer = AuthFeature.reducer::invoke,
            effectHandlers = arrayOf(authEffectHandler.adaptCast()),
        ).apply { dispatch(AuthFeature.Msg.Init) }

}
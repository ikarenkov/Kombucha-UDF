package io.github.ikarenkov.sample.shikimori.impl.auth

import io.github.ikarenkov.kombucha.test.ReducerTestData
import io.github.ikarenkov.kombucha.test.testReducer
import io.github.ikarenkov.kombucha.test.testStoreReducer
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature.Eff
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature.Msg
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature.State
import io.mockk.mockk
import org.junit.jupiter.api.Test

class AuthFeatureTest {

    @Test
    fun testAuthStoreReducer() {
        testStoreReducer(
            createStore = { AuthStore(it, mockk()) }
        ) {
            Msg.LoadCacheAuthResult(null) returns State.NotAuthorized.Idle
            Msg.Auth returns State.NotAuthorized.Idle + Eff.GetAuthorizationCodeBrouser
        }
    }

    @Test
    fun testAuthReducerDsl() {
        testReducer(
            initialState = State.Init(false),
            reducer = AuthFeature.Reducer
        ) {
            Msg.Init returns State.Init(inProgress = true) + Eff.LoadCachedData
            Msg.LoadCacheAuthResult(null) returns State.NotAuthorized.Idle
        }
    }

    @Test
    fun testAuthReducer() {
        testReducer(
            initialState = State.Init(false),
            reducer = AuthFeature.Reducer,
            expectedReducerResults = listOf(
                ReducerTestData(Msg.Init, State.Init(inProgress = true), setOf(Eff.LoadCachedData)),
                ReducerTestData(Msg.LoadCacheAuthResult(null), State.NotAuthorized.Idle)
            )
        )
    }

}
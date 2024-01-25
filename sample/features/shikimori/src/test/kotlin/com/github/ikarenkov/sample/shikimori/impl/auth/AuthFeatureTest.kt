package com.github.ikarenkov.sample.shikimori.impl.auth

import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature.Eff
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature.Msg
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature.State
import io.mockk.mockk
import org.junit.jupiter.api.Test
import ru.ikarenkov.kombucha.test.ReducerTestData
import ru.ikarenkov.kombucha.test.testReducer
import ru.ikarenkov.kombucha.test.testStoreReducer

class AuthFeatureTest {

    @Test
    fun testAuthStoreReducer() {
        testStoreReducer(
            createStore = { AuthFeature(it, mockk()) }
        ) {
            Msg.LoadCacheAuthResult(null) returns State.NotAuthorized.Idle
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
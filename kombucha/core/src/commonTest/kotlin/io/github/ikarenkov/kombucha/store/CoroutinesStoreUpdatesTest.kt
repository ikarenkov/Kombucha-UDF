package io.github.ikarenkov.kombucha.store

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.eff_handler.adaptCast
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.TestCoroutinesFeature.Eff
import io.github.ikarenkov.kombucha.store.TestCoroutinesFeature.Msg
import io.github.ikarenkov.kombucha.store.TestCoroutinesFeature.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CoroutinesStoreUpdatesTest {

    @Test
    @JsName("test1")
    fun `When accept msg - Than update state`() {
        val store = TestStore<Msg, State, Nothing>(
            initialState = State(value = 0),
            reducer = dslReducer { msg ->
                when (msg) {
                    Msg.Reset -> state { State(0) }
                    is Msg.Plus -> state { copy(value + msg.value) }
                    is Msg.Set -> state { State(msg.value) }
                }
            },
            effectHandlers = listOf()
        )
        runTest {
            val states = mutableListOf<State>()
            val job = launch { store.state.collect { states += it } }
            // collect first state
            advanceUntilIdle()
            listOf(
                Msg.Plus(10),
                Msg.Set(5),
                Msg.Reset,
            ).forEach {
                store.accept(it)
                store.coroutinesScope.advanceUntilIdle()
                advanceUntilIdle()
            }
            assertContentEquals(
                listOf(State(0), State(10), State(5), State(0)),
                states
            )
            job.cancel()
        }
    }

    @Test
    @JsName("test2")
    fun `When emit effects - Then receive it in EffectHandler`() {
        val effectHandler1 = CountCallsEffectHandler<Eff, Msg>()
        val effectHandler2 = CountCallsEffectHandler<Eff, Msg>()
        val store = TestStore<Msg, State, Eff>(
            initialState = State(value = 0),
            reducer = dslReducer { msg -> eff(Eff.A) },
            effectHandlers = listOf(effectHandler1, effectHandler2)
        )
        runTest {
            val states = mutableListOf<State>()
            val job = launch {
                store.state.collect { states += it }
            }
            // collect first state
            advanceUntilIdle()
            listOf(
                Msg.Plus(10),
            ).forEach {
                store.accept(it)
                store.coroutinesScope.advanceUntilIdle()
                advanceUntilIdle()
            }
            assertContentEquals(listOf(State(0)), states)
            assertEquals(1, effectHandler1.handleEffCallCount)
            assertEquals(1, effectHandler2.handleEffCallCount)
            job.cancel()
        }
    }

    @Test
    @JsName("test3")
    fun `Test adapt cast effect handler in coroutines store`() {
        val effectHandler = CountCallsEffectHandler<Eff, Msg>()
        val specificEffectHandler = CountCallsEffectHandler<Eff.A, Msg>()
        val store = TestStore<Msg, State, Eff>(
            initialState = State(value = 0),
            reducer = dslReducer { msg ->
                when (msg) {
                    is Msg.Plus -> eff(Eff.A)
                    Msg.Reset -> eff(Eff.B(1))
                    is Msg.Set -> eff(Eff.C(1))
                }
            },
            effectHandlers = listOf(effectHandler, specificEffectHandler.adaptCast())
        )

        store.accept(Msg.Reset)
        store.coroutinesScope.advanceUntilIdle()

        assertEquals(1, effectHandler.handleEffCallCount)
        assertEquals(0, specificEffectHandler.handleEffCallCount)

        store.accept(Msg.Plus(10))
        store.coroutinesScope.advanceUntilIdle()

        assertEquals(2, effectHandler.handleEffCallCount)
        assertEquals(1, specificEffectHandler.handleEffCallCount)

        store.accept(Msg.Set(10))
        store.coroutinesScope.advanceUntilIdle()

        assertEquals(3, effectHandler.handleEffCallCount)
        assertEquals(1, specificEffectHandler.handleEffCallCount)
    }

    class CountCallsEffectHandler<Eff : Any, Msg : Any> : EffectHandler<Eff, Msg> {

        var handleEffCallCount: Int = 0

        override fun handleEff(eff: Eff): Flow<Msg> {
            handleEffCallCount++
            return emptyFlow()
        }

    }
}

private object TestCoroutinesFeature {

    sealed interface Msg {
        data object Reset : Msg
        data class Plus(val value: Int) : Msg
        data class Set(val value: Int) : Msg
    }

    data class State(
        val value: Int
    )

    sealed interface Eff {

        data object A : Eff
        data class B(val value: Int) : Eff
        data class C(val value: Int) : Eff

    }

}
package io.github.ikarenkov.kombucha.store

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.TestLoadFeature.Eff
import io.github.ikarenkov.kombucha.store.TestLoadFeature.Msg
import io.github.ikarenkov.kombucha.store.TestLoadFeature.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertContentEquals

class CoroutinesStoreEffectHandlersTest {

    @Test
    @JsName("test1")
    fun `When effect handler send msg - Then it comes to reducer`() = runTest {
        val effectHandler = LoadEffectHandler(Msg.LoadingResult(5))
        val store = TestStore<Msg, State, Eff>(
            reducer = dslReducer { msg ->
                when (msg) {
                    is Msg.LoadingResult -> state { State(msg.value) }
                    Msg.Load -> eff(Eff.Load)
                }
            },
            initialState = State(0),
            effectHandlers = listOf(effectHandler)
        )

        val states = mutableListOf<State>()
        val job = launch { store.state.collect { states += it } }

        // first collect
        advanceUntilIdle()
        store.accept(Msg.Load)

        assertContentEquals(listOf(State(0)), states)

        store.coroutinesScope.advanceTimeBy(101)
        advanceUntilIdle()

        assertContentEquals(listOf(State(0), State(5)), states)

        job.cancel()
    }

}

private class LoadEffectHandler(private val msgToEmit: Msg) : EffectHandler<Eff, Msg> {
    override fun handleEff(eff: Eff): Flow<Msg> = flow {
        delay(100)
        emit(msgToEmit)
    }
}

private object TestLoadFeature {

    sealed interface Msg {
        data object Load : Msg
        data class LoadingResult(val value: Int) : Msg
    }

    data class State(val value: Int)

    sealed interface Eff {
        data object Load : Eff
    }

}
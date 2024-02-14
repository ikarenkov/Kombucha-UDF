package io.github.ikarenkov.kombucha.eff_handler

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class EffectHandlerAdapterTest {

    @Test
    @JsName("test1")
    fun `When adapt eff to null - Then no eff comes to original effect handler`() {
        val effectHandler = CountCallsEffectHandler<Any, Any> { Any() }
        val adoptedEffectHandler = effectHandler.adapt<Any, Any, Any, Any>(
            effAdapter = { null },
            msgAdapter = { it },
        )

        adoptedEffectHandler.handleEff(Any())
        adoptedEffectHandler.handleEff(Any())

        assertEquals(0, effectHandler.handleEffCallCount)
    }

    @Test
    @JsName("test2")
    fun `When adapt msg to null - Then no msg comes from adopted effect handler`() = runTest {
        val effectHandler = CountCallsEffectHandler<Int, Long> { it.toLong() }
        val adoptedEffectHandler = effectHandler.adapt<Int, Long, Any, Any>(
            effAdapter = { it as? Int },
            msgAdapter = { null },
        )

        val flow = adoptedEffectHandler.handleEff(2)
        val list = flow.toList(mutableListOf())

        assertEquals(1, effectHandler.handleEffCallCount)
        assertContentEquals(emptyList(), list)
    }

    @Test
    @JsName("test3")
    fun `When adapt msg to not null - Then msg comes from adopted effect handler`() = runTest {
        val effectHandler = CountCallsEffectHandler<Int, Long> { it.toLong() }
        val adoptedEffectHandler = effectHandler.adapt<Int, Long, Any, Any>(
            effAdapter = { it as? Int },
            msgAdapter = { it },
        )

        val flow = adoptedEffectHandler.handleEff(2)
        val list = flow.toList(mutableListOf())

        assertEquals(1, effectHandler.handleEffCallCount)
        assertContentEquals(listOf(2L), list)
    }

    @Test
    @JsName("test4")
    fun `When adapt portion of msg to not null - Then only specific msg comes from adopted effect handler`() = runTest {
        val effectHandler = CountCallsEffectHandler<Int, Long> { it.toLong() }
        val adoptedEffectHandler = effectHandler.adapt<Int, Long, Any, Any>(
            effAdapter = { (it as? Int)?.takeIf { it % 2 == 0 } },
            msgAdapter = { it },
        )

        val flow1 = adoptedEffectHandler.handleEff(2)
        val list1 = flow1.toList(mutableListOf())

        assertEquals(1, effectHandler.handleEffCallCount)
        assertContentEquals(listOf(2L), list1)

        val flow2 = adoptedEffectHandler.handleEff(1)
        val list2 = flow2.toList(mutableListOf())

        assertEquals(1, effectHandler.handleEffCallCount)
        assertContentEquals(listOf(), list2)
    }

    class CountCallsEffectHandler<Eff : Any, Msg : Any>(private val effToMsg: (Eff) -> Msg) : EffectHandler<Eff, Msg> {

        var handleEffCallCount: Int = 0

        override fun handleEff(eff: Eff): Flow<Msg> {
            handleEffCallCount++
            return flow { emit(effToMsg(eff)) }
        }

    }

}
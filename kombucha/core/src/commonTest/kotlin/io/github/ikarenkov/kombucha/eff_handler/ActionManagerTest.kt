@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.ikarenkov.kombucha.eff_handler

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ActionManagerTest {

    private lateinit var effHandler: ActionManagerTestEffectHandler

    @BeforeTest
    fun beforeEachTest() {
        effHandler = ActionManagerTestEffectHandler()
    }

    @Test
    @JsName("test1")
    fun `When 3 effects have sent - Then only last handled`() = runTest {
        val messages = mutableListOf<ActionManagerTestMsg>()

        launch { effHandler.handleEff(ActionManagerTestEff.CounterEff(1)).toCollection(messages) }
        launch { effHandler.handleEff(ActionManagerTestEff.CounterEff(2)).toCollection(messages) }
        launch { effHandler.handleEff(ActionManagerTestEff.CounterEff(3)).toCollection(messages) }

        advanceUntilIdle()

        assertEquals(1, messages.size)
        assertEquals(ActionManagerTestMsg.CounterMsg(3), messages.first())
    }

    @Test
    @JsName("test2")
    fun `When 1 effect have sent - Then it is handled`() = runTest {
        val messages = mutableListOf<ActionManagerTestMsg>()

        launch { effHandler.handleEff(ActionManagerTestEff.CounterEff(1)).toCollection(messages) }

        advanceUntilIdle()

        assertEquals(1, messages.size)
        assertEquals(ActionManagerTestMsg.CounterMsg(1), messages.first())
    }

}

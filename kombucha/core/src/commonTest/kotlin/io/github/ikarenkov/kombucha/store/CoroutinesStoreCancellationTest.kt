package io.github.ikarenkov.kombucha.store

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class CoroutinesStoreCancellationTest {

    @Test
    @JsName("test1")
    fun `When accept on canceled store - Then crush`() {
        val store = NoOpTestStore()
        store.accept(Any())
        store.cancel()
        assertFails {
            store.accept(Any())
        }
    }

    @Test
    @JsName("test2")
    fun `When canceled - Then isActive false`() {
        val store = NoOpTestStore()
        store.cancel()
        assertEquals(false, store.isActive)
    }

    internal class NoOpTestStore : CoroutinesStore<Any, Any, Nothing>(
        name = "NoOpTestStore",
        reducer = { _, state -> state to emptySet() },
        initialState = Any(),
    )

}
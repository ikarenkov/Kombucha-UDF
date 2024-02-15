package io.github.ikarenkov.kombucha.ui

import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.CoroutinesStore
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UiStoreTest {

    @Test
    @JsName("test1")
    fun `When cancel original is false - original is not canceled`() {
        val store = DummyStore()
        val uiStore = store.uiBuilder()
            .using<Any, Any, Any>(
                cancelOriginalStoreOnCancel = false,
                uiDispatcher = StandardTestDispatcher()
            ) { it }

        uiStore.cancel()

        assertTrue(store.isActive)
    }

    @Test
    @JsName("test2")
    fun `When cancel original is true - original is canceled`() {
        val store = DummyStore()
        val uiStore = store.uiBuilder()
            .using<Any, Any, Any>(
                cancelOriginalStoreOnCancel = true,
                uiDispatcher = StandardTestDispatcher()
            ) { it }

        uiStore.cancel()

        assertFalse(store.isActive)
    }

    class DummyStore : CoroutinesStore<Any, Any, Any>(
        name = "DummyStore",
        reducer = dslReducer { },
        initialState = Any()
    )

}
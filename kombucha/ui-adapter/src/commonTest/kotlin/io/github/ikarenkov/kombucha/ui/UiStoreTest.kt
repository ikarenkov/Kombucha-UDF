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
    fun `When close original is false - original is not closed`() {
        val store = DummyStore()
        val uiStore = store.uiBuilder()
            .using<Any, Any, Any>(
                propagateCloseToOriginal = false,
                uiDispatcher = StandardTestDispatcher()
            ) { it }

        uiStore.close()

        assertTrue(store.isActive)
    }

    @Test
    @JsName("test2")
    fun `When close original is true - original is closed`() {
        val store = DummyStore()
        val uiStore = store.uiBuilder()
            .using<Any, Any, Any>(
                propagateCloseToOriginal = true,
                uiDispatcher = StandardTestDispatcher()
            ) { it }

        uiStore.close()

        assertFalse(store.isActive)
    }

    class DummyStore : CoroutinesStore<Any, Any, Any>(
        name = "DummyStore",
        reducer = dslReducer { },
        initialState = Any()
    )

}
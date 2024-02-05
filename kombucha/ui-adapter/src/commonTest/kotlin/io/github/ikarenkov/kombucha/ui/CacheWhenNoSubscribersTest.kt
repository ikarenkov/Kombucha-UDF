package io.github.ikarenkov.kombucha.ui

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

class CacheWhenNoSubscribersTest {

    @Test
    fun TestSeveralSubscriptions() {
        val originalFlow = flow {
            emit(1)
            delay(100)
            emit(2)
            delay(100)
            emit(3)
            delay(1000)
            emit(4)
            emit(5)
        }

        val coroutineScope = TestScope()
        val cachedFlow = originalFlow.cacheWhenNoSubscribers(coroutineScope)

        val (job1, list1) = cachedFlow.collectToList(coroutineScope)
        coroutineScope.advanceTimeBy(101)
        assertEquals(listOf(1, 2), list1)

        val (job2, list2) = cachedFlow.collectToList(coroutineScope)
        coroutineScope.advanceTimeBy(201)

        assertEquals(listOf(3), list2)
        assertEquals(listOf(1, 2, 3), list1)

        // cancel all subscriptions - caching must start after it
        job1.cancel()
        job2.cancel()
        coroutineScope.advanceTimeBy(2000)

        val (_, list3) = cachedFlow.collectToList(coroutineScope)
        coroutineScope.advanceTimeBy(1000)
        assertEquals(listOf(4, 5), list3)
    }

    @Test
    @JsName("test2")
    fun `Test caching before first subscription`() {
        val originalFlow = flow {
            emit(1)
            delay(100)
            emit(2)
            delay(100)
            emit(3)
            delay(1000)
            emit(4)
            emit(5)
        }

        val coroutineScope = TestScope()
        val cachedFlow = originalFlow.cacheWhenNoSubscribers(coroutineScope)

        coroutineScope.advanceTimeBy(201)

        val (_, list) = cachedFlow.collectToList(coroutineScope)
        coroutineScope.advanceTimeBy(1)
        assertEquals(listOf(1, 2, 3), list)
    }

    @Test
    @JsName("test3")
    fun `When scope is canceled - no more events`() = runTest {
        val originalFlow = flow {
            emit(1)
            delay(100)
            emit(2)
            delay(100)
            emit(3)
            delay(1000)
            emit(4)
            emit(5)
        }

        val coroutineScope = TestScope()
        val cachedFlow = originalFlow.cacheWhenNoSubscribers(coroutineScope)

        val (job1, list1) = cachedFlow.collectToList(coroutineScope)
        coroutineScope.advanceTimeBy(101)
        assertEquals(listOf(1, 2), list1)

        coroutineScope.cancel()

        coroutineScope.advanceTimeBy(2000)
        assertEquals(listOf(1, 2), list1)

        val (job2, list2) = cachedFlow.collectToList(coroutineScope)
        coroutineScope.advanceTimeBy(2000)
        assertEquals(listOf(), list2)
    }

    private fun <T> Flow<T>.collectToList(coroutineScope: TestScope): Pair<Job, MutableList<T>> {
        val list = mutableListOf<T>()
        val job = coroutineScope.launch {
            collect {
                list += it
            }
        }
        return job to list
    }

}
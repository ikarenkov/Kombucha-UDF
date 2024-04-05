package io.github.ikarenkov.kombucha.test

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.reducer.Reducer
import io.github.ikarenkov.kombucha.store.CoroutinesStore
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.ReducerStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.assertContentEquals

@OptIn(ExperimentalCoroutinesApi::class)
fun <Msg : Any, State : Any, Eff : Any> testStoreReducer(
    createStore: (ReducerStoreFactory) -> Store<Msg, State, Eff>,
    testData: List<ReducerTestData<Msg, State, Eff>>
) {
    runTest {
        val storeFactory = TestStoreFactory<Msg, State, Eff>()
        val store = createStore(storeFactory)
        val collectedUpdates = mutableListOf<ReducerTestData<Msg, State, Eff>>()
        val collectionJob = launch {
            storeFactory.createdStore!!.reducerUpdates
                .drop(1)
                .map { (msg, _, newState, effects) -> ReducerTestData(msg, newState, effects) }
                .collect { collectedUpdates += it }
        }
        testData.forEach {
            store.accept(it.msg)
        }
        // launch collecting results
        advanceUntilIdle()
        // launch handling incoming messages
        storeFactory.testScope.advanceUntilIdle()
        // collect new messages
        advanceUntilIdle()
        collectionJob.cancel()
        assertContentEquals(
            testData,
            collectedUpdates
        )
    }
}

class TestStoreFactory<FMsg : Any, FState : Any, FEff : Any>(
    val testScope: TestScope = TestScope()
) : ReducerStoreFactory {

    var createdStore: CoroutinesStore<FMsg, FState, FEff>? = null

    override fun <Msg : Any, State : Any, Eff : Any> create(
        name: String?,
        initialState: State,
        reducer: Reducer<Msg, State, Eff>,
        initialEffects: Set<Eff>,
        vararg effectHandlers: EffectHandler<Eff, Msg>
    ): Store<Msg, State, Eff> = object : CoroutinesStore<Msg, State, Eff>(
        name = name,
        reducer = reducer,
        effectHandlers = emptyList(),
        initialState = initialState,
        initialEffects = initialEffects
    ) {
        override val coroutinesScope: CoroutineScope = testScope
    }.also { createdStore = it as CoroutinesStore<FMsg, FState, FEff> }

}
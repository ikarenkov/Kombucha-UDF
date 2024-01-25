package ru.ikarenkov.kombucha.store

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.ikarenkov.kombucha.eff_handler.FlowEffectHandler
import kotlin.coroutines.EmptyCoroutineContext

open class CoroutinesStore<Msg : Any, Model : Any, Eff : Any>(
    name: String?,
    private val reducer: (Model, Msg) -> Pair<Model, Set<Eff>>,
    private val effectHandlers: List<FlowEffectHandler<Eff, Msg>> = listOf(),
    initialState: Model,
    initialEffects: Set<Eff> = setOf(),
    coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        System.err.println("Unhandled error in Coroutine store named \"$name\".")
        throwable.printStackTrace()
    }
) : Store<Msg, Model, Eff> {

    private val mutableState = MutableStateFlow(initialState)
    override val state: StateFlow<Model> = mutableState

    private val mutableEffects = MutableSharedFlow<Eff>()
    override val effects: Flow<Eff> = mutableEffects

    private val isCanceled: Boolean
        get() = !coroutinesScope.isActive

    protected open val coroutinesScope = CoroutineScope(
        SupervisorJob() +
                coroutineExceptionHandler +
                (name?.let { CoroutineName(name) } ?: EmptyCoroutineContext)
    )

    private val stateUpdateMutex = Mutex()

    private val mutableStoreUpdates: MutableSharedFlow<StoreUpdate<Msg, Model, Eff>> = MutableSharedFlow()
    val storeUpdates: SharedFlow<StoreUpdate<Msg, Model, Eff>> = mutableStoreUpdates

    init {
        initEffHandlers(initialEffects)
    }

    override fun dispatch(msg: Msg) {
        coroutinesScope.launch {
            val storeUpdate = stateUpdateMutex.withLock {
                if (!isCanceled) {
                    val oldState = state.value
                    val (newState, effects) = reducer(oldState, msg)
                    mutableState.value = newState
                    StoreUpdate(
                        msg = msg,
                        oldState = oldState,
                        newState = newState,
                        effects = effects
                    )
                } else {
                    null
                }
            }
            if (storeUpdate != null) {
                mutableStoreUpdates.emit(storeUpdate)
                storeUpdate.effects.forEach { eff ->
                    launch {
                        mutableEffects.emit(eff)
                        handleEff(eff)
                    }
                }
            }
        }
    }

    override fun cancel() {
        coroutinesScope.cancel()
    }

    private fun initEffHandlers(initialEffects: Set<Eff>) {
        initialEffects.forEach {
            handleEff(it)
        }
    }

    private fun handleEff(eff: Eff) {
        effectHandlers.forEach { effHandler ->
            coroutinesScope.launch {
                effHandler
                    .handleEff(eff = eff)
                    .collect { dispatch(it) }
            }
        }
    }

}

data class StoreUpdate<Msg, State, Eff>(
    val msg: Msg,
    val oldState: State,
    val newState: State,
    val effects: Set<Eff>
)

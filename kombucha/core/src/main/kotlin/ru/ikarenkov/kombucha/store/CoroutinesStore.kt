package ru.ikarenkov.kombucha.store

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val effHandlers: List<FlowEffectHandler<Eff, Msg>> = listOf(),
    initialState: Model,
    initialEffects: Set<Eff> = setOf(),
    coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        System.err.println("Unhandled error in Coroutine store named \"$name\".")
        throwable.printStackTrace()
    }
) : Store<Msg, Model, Eff> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<Model> = _state

    private val _effects = MutableSharedFlow<Eff>()
    override val effects: Flow<Eff> = _effects

    private val isCanceled: Boolean
        get() = !coroutinesScope.isActive

    private val coroutinesScope = CoroutineScope(
        SupervisorJob() +
                coroutineExceptionHandler +
                (name?.let { CoroutineName(name) } ?: EmptyCoroutineContext)
    )

    private val stateUpdateMutex = Mutex()

    init {
        initEffHandlers(initialEffects)
    }

    override fun dispatch(msg: Msg) {
        coroutinesScope.launch {
            val effs = stateUpdateMutex.withLock {
                if (!isCanceled) {
                    val (newState, effs) = reducer(state.value, msg)
                    _state.value = newState
                    effs
                } else {
                    null
                }
            }
            effs?.forEach { eff ->
                _effects.emit(eff)
                handleEff(eff)
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
        effHandlers.forEach { effHandler ->
            coroutinesScope.launch {
                effHandler
                    .handleEff(eff = eff)
                    .collect { dispatch(it) }
            }
        }
    }

}

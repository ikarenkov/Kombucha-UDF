package ru.ikarenkov.kombucha.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.ikarenkov.kombucha.Cancelable
import ru.ikarenkov.kombucha.eff_handler.EffectHandler
import java.util.concurrent.ConcurrentHashMap

class SyncStore<Msg : Any, Model : Any, Eff : Any>(
    initialState: Model,
    private val reducer: (Model, Msg) -> Pair<Model, Set<Eff>>,
    private val effHandlers: List<EffectHandler<Eff, Msg>> = listOf(),
    initialEffects: Set<Eff> = setOf(),
) : Store<Msg, Model, Eff> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<Model> = _state

    private val _effects = MutableSharedFlow<Eff>()
    override val effects: Flow<Eff> = _effects

    private var isCanceled = false
    private val cancelables = ConcurrentHashMap<Cancelable, Cancelable>()

    init {
        initEffHandlers(initialEffects)
    }

    @Synchronized
    override fun dispatch(msg: Msg) {
        if (isCanceled) {
            return
        }
        val (newState, effs) = reducer(state.value, msg)
        _state.value = newState
        effs.forEach(::handleEff)
    }

    @Synchronized
    override fun cancel() {
        isCanceled = true
        cancelables.forEach {
            it.value.cancel()
        }
        cancelables.clear()
    }

    private fun initEffHandlers(initialEffects: Set<Eff>) {
        initialEffects.forEach {
            handleEff(it)
        }
    }

    private fun handleEff(eff: Eff) {
        effHandlers.forEach { effHandler ->
            val emmitTerminate = EmmitTerminate()
            val cancelable = effHandler.handleEff(
                eff = eff,
                emmit = ::dispatch,
                emmitTerminate = {
                    dispatch(it)
                    emmitTerminate.cancel()
                }
            )
            cancelables[cancelable] = cancelable
            emmitTerminate.cancelable = Cancelable {
                cancelables.remove(cancelable)
            }
        }
    }

    class EmmitTerminate : Cancelable {

        @Volatile
        var cancelable: Cancelable? = null
            @Synchronized
            set(value) {
                if (isCanceled) {
                    value?.cancel()
                }
                field = value
            }

        @Volatile
        private var isCanceled = false

        @Synchronized
        override fun cancel() {
            isCanceled = true
            cancelable?.cancel()
        }

    }

}

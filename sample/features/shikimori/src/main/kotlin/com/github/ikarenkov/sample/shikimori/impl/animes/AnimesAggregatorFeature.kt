package com.github.ikarenkov.sample.shikimori.impl.animes

import com.github.ikarenkov.sample.shikimori.api.ShikimoriDeps
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthStore
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationFeature
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationStore
import com.github.ikarenkov.sample.shikimori.impl.storeCoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.ikarenkov.teamaker.store.AggregatorStore

internal class AnimesAggregatorFeature(
    private val animesFeature: AnimesFeature,
    private val paginationStore: PaginationStore<AnimesFeatureAgregatorFactory.Anime>,
    private val authStore: AuthStore,
    private val deps: ShikimoriDeps
) : AggregatorStore<AnimesAggregatorFeature.Msg, AnimesAggregatorFeature.State, AnimesAggregatorFeature.Eff>(
    name = "AnimesAggregator",
    coroutineExceptionHandler = storeCoroutineExceptionHandler("AnimesAggregator")
) {

    override val state: StateFlow<State>
        get() =
            combine(paginationStore.state, animesFeature.state, ::State)
                .stateIn(
                    scope = scope,
                    started = SharingStarted.Eagerly,
                    initialValue = State(paginationStore.state.value, animesFeature.state.value)
                )

    private val _effects: MutableSharedFlow<Eff> = MutableSharedFlow()
    override val effects: Flow<Eff> = _effects

    init {
        scope.launch {
            animesFeature.effects.collect {
                _effects.emit(Eff.Animes(it))
            }
        }
        scope.launch {
            paginationStore.effects.collect {
                _effects.emit(Eff.Pagination(it))
            }
        }
        scope.launch {
            animesFeature.effects.collect { eff ->
                when (eff) {
                    AnimesFeature.Eff.Authorize -> authStore.dispatch(AuthFeature.Msg.Auth)
                }
            }
        }
        scope.launch {
            animesFeature.effects.collect { eff ->
                when (eff) {
                    AnimesFeature.Eff.Authorize -> authStore.dispatch(AuthFeature.Msg.Auth)
                }
            }
        }
    }

    override fun dispatch(msg: Msg) {
        when (msg) {
            is Msg.Animes -> animesFeature.dispatch(msg.msg)
            is Msg.Pagination -> paginationStore.dispatch(msg.msg)
        }
    }

    override fun cancel() {
        paginationStore.cancel()
        animesFeature.cancel()
    }

    data class State(
        val paginationState: PaginationFeature.State<AnimesFeatureAgregatorFactory.Anime>,
        val animesState: AnimesFeature.State
    )

    sealed interface Msg {

        data class Pagination(val msg: PaginationFeature.Msg) : Msg
        data class Animes(val msg: AnimesFeature.Msg) : Msg

    }

    sealed interface Eff {

        data class Pagination(val msg: PaginationFeature.Eff) : Eff
        data class Animes(val msg: AnimesFeature.Eff) : Eff

    }
}
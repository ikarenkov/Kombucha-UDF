package com.github.ikarenkov.sample.shikimori.impl.animes

import com.github.ikarenkov.sample.shikimori.api.ShikimoriDeps
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthStore
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationFeature
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationStore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import ru.ikarenkov.teamaker.store.AggregatorStore

internal class AnimesAggregatorFeature(
    private val animesFeature: AnimesFeature,
    private val paginationStore: PaginationStore<AnimesFeatureAgregatorFactory.Anime>,
    private val authStore: AuthStore,
    private val deps: ShikimoriDeps
) : AggregatorStore<AnimesAggregatorFeature.Msg, AnimesAggregatorFeature.State, AnimesAggregatorFeature.Eff>(
    name = "AnimesAggregator",
    coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logcat(
            priority = LogPriority.ERROR,
            tag = "AnimesAggregator"
        ) { throwable.asLog() }
    }
) {

    override val state: StateFlow<State>
        get() =
            combine(paginationStore.state, animesFeature.state) { paginationState, authState ->
                State(paginationState, authState)
            }
                .stateIn(
                    scope = scope,
                    started = SharingStarted.Eagerly,
                    initialValue = State(paginationStore.state.value, animesFeature.state.value)
                )

    override val effects: Flow<Eff>
        get() = emptyFlow()

    init {

        // move to init of auth
//        scope.launch {
//            deps.intents().collect {
//                animesFeature.dispatch(msg = AuthFeature.Msg.OAuthResult(it.data.toString()))
//            }
//        }
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
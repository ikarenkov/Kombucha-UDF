package io.github.ikarenkov.sample.shikimori.impl.animes

import io.github.ikarenkov.kombucha.aggregator.AggregatorStore
import io.github.ikarenkov.kombucha.aggregator.bindEffToMsg
import io.github.ikarenkov.kombucha.aggregator.bindStateToMsg
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import io.github.ikarenkov.sample.shikimori.impl.pagination.PaginationFeature
import io.github.ikarenkov.sample.shikimori.impl.storeCoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

internal class AnimesAggregatorFeature(
    private val animesFeature: AnimesFeature,
    private val paginationStore: PaginationFeature<AnimesFeatureAgregatorFactory.Anime>,
    private val authFeature: AuthFeature,
) : AggregatorStore<AnimesAggregatorFeature.Msg, AnimesAggregatorFeature.State, AnimesAggregatorFeature.Eff>(
    name = "AnimesAggregator",
    coroutineExceptionHandler = storeCoroutineExceptionHandler("AnimesAggregator")
) {

    override val state: StateFlow<State> =
        combine(paginationStore.state, animesFeature.state, AnimesAggregatorFeature::State)
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Lazily,
                initialValue = State(paginationStore.state.value, animesFeature.state.value)
            )

    override val effects: Flow<Eff> = merge(
        animesFeature.effects.map { Eff.Animes(it) },
        paginationStore.effects.map { Eff.Pagination(it) }
    )

    init {
        bindEffToMsg(coroutineScope, animesFeature, authFeature) { eff ->
            when (eff) {
                AnimesFeature.Eff.Authorize -> AuthFeature.Msg.Auth
            }
        }
        bindStateToMsg(coroutineScope, authFeature, animesFeature) { state ->
            when (state) {
                is AuthFeature.State.Authorized -> AnimesFeature.Msg.AuthorizationResult.Authorized
                is AuthFeature.State.Init, is AuthFeature.State.NotAuthorized.OAuthInProgress -> AnimesFeature.Msg.AuthorizationResult.Loading
                AuthFeature.State.NotAuthorized.Idle -> AnimesFeature.Msg.AuthorizationResult.NotAuthorized
            }
        }
    }

    override fun accept(msg: Msg) {
        when (msg) {
            is Msg.Animes -> animesFeature.accept(msg.msg)
            is Msg.Pagination -> paginationStore.accept(msg.msg)
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
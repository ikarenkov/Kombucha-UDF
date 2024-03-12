package io.github.ikarenkov.sample.shikimori.impl.animes

import io.github.ikarenkov.kombucha.aggregator.AggregatorStore
import io.github.ikarenkov.kombucha.aggregator.bindEffToMsg
import io.github.ikarenkov.kombucha.aggregator.bindStateToMsg
import io.github.ikarenkov.sample.core.pagination.PaginationEff
import io.github.ikarenkov.sample.core.pagination.PaginationMsg
import io.github.ikarenkov.sample.core.pagination.PaginationState
import io.github.ikarenkov.sample.core.pagination.PaginationStore
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthStore
import io.github.ikarenkov.sample.shikimori.impl.storeCoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

internal class AnimesAggregatorStore(
    private val animesStore: AnimesStore,
    private val paginationStore: PaginationStore<AnimesStoreAgregatorFactory.Anime>,
    authStore: AuthStore,
) : AggregatorStore<AnimesAggregatorStore.Msg, AnimesAggregatorStore.State, AnimesAggregatorStore.Eff>(
    name = "AnimesAggregator",
    coroutineExceptionHandler = storeCoroutineExceptionHandler("AnimesAggregator")
) {

    override val state: StateFlow<State> =
        combineStates(paginationStore.state, animesStore.state, AnimesAggregatorStore::State)

    override val effects: Flow<Eff> = merge(
        animesStore.effects.map { Eff.Animes(it) },
        paginationStore.effects.map { Eff.Pagination(it) }
    )

    init {
        bindEffToMsg(animesStore, authStore) { eff ->
            when (eff) {
                AnimesFeature.Eff.Authorize -> AuthFeature.Msg.Auth
            }
        }
        bindStateToMsg(authStore, animesStore) { state ->
            when (state) {
                is AuthFeature.State.Authorized -> AnimesFeature.Msg.AuthorizationResult.Authorized
                is AuthFeature.State.Init, is AuthFeature.State.NotAuthorized.OAuthInProgress -> AnimesFeature.Msg.AuthorizationResult.Loading
                AuthFeature.State.NotAuthorized.Idle -> AnimesFeature.Msg.AuthorizationResult.NotAuthorized
            }
        }
    }

    override fun accept(msg: Msg) {
        when (msg) {
            is Msg.Animes -> animesStore.accept(msg.msg)
            is Msg.Pagination -> paginationStore.accept(msg.msg)
        }
    }

    override fun close() {
        super.close()
        paginationStore.close()
        animesStore.close()
    }

    data class State(
        val paginationState: PaginationState<AnimesStoreAgregatorFactory.Anime>,
        val animesState: AnimesFeature.State
    )

    sealed interface Msg {

        data class Pagination(val msg: PaginationMsg<AnimesStoreAgregatorFactory.Anime>) : Msg
        data class Animes(val msg: AnimesFeature.Msg) : Msg

    }

    sealed interface Eff {

        data class Pagination(val msg: PaginationEff) : Eff
        data class Animes(val msg: AnimesFeature.Eff) : Eff

    }
}
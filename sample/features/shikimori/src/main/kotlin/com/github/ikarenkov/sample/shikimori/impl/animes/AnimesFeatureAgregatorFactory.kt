package com.github.ikarenkov.sample.shikimori.impl.animes

import com.github.ikarenkov.sample.shikimori.api.ShikimoriDeps
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import com.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationEffectHandler
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationFeature
import ru.ikarenkov.teamaker.eff_handler.adaptCast
import ru.ikarenkov.teamaker.store.Store
import ru.ikarenkov.teamaker.store.StoreFactory

internal class AnimesFeatureAgregatorFactory(
    private val storeFactory: StoreFactory,
    private val animesDataFetcher: AnimesDataFetcher,
    private val authEffectHandler: AuthFeature.AuthEffHandler,
    private val deps: ShikimoriDeps
) {

    fun createStore(): Store<AnimesAggregatorFeature.Msg, AnimesAggregatorFeature.State, AnimesAggregatorFeature.Eff> {
        val paginationFeature = PaginationFeature.create(
            storeFactory = storeFactory,
            name = "AnimePagination",
            dataFetcher = animesDataFetcher
        )
        val authFeature = storeFactory.create<AuthFeature.Msg, AuthFeature.State, AuthFeature.Eff>(
            name = "AuthFeature",
            initialState = AuthFeature.State.NotAuthorized,
            reducer = AuthFeature.reducer::invoke,
            effectHandlers = arrayOf(authEffectHandler.adaptCast()),
        )
        return AnimesAggregatorFeature(AnimesFeature(), paginationFeature, authFeature, deps)
    }

    data class Anime(val id: String, val name: String)

    class AnimesDataFetcher(
        private val api: ShikimoriBackendApi
    ) : PaginationEffectHandler.DataFetcher<Anime> {

        override suspend fun fetch(input: PaginationFeature.Eff.Load): PaginationFeature.Msg.Internal.LoadResult<Anime> =
            api
                .animes(input.page, input.size)
                .map { animes ->
                    animes.map { Anime(it.id.toString(), it.name) }
                }
                .let {
                    PaginationFeature.Msg.Internal.LoadResult<Anime>(it, input.page, input.size)
                }
    }

}
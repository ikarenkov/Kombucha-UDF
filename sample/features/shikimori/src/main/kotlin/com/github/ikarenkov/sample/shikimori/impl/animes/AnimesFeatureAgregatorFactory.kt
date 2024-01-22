package com.github.ikarenkov.sample.shikimori.impl.animes

import com.github.ikarenkov.sample.shikimori.api.ShikimoriDeps
import com.github.ikarenkov.sample.shikimori.api.shikimoriFeatureFacade
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import com.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationEffectHandler
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationFeature
import ru.ikarenkov.kombucha.store.Store
import ru.ikarenkov.kombucha.store.StoreFactory

internal class AnimesFeatureAgregatorFactory(
    private val storeFactory: StoreFactory,
    private val animesDataFetcher: AnimesDataFetcher,
) {

    fun createStore(): Store<AnimesAggregatorFeature.Msg, AnimesAggregatorFeature.State, AnimesAggregatorFeature.Eff> {
        val paginationFeature = PaginationFeature(
            storeFactory = storeFactory,
            name = "AnimePagination",
            dataFetcher = animesDataFetcher
        )
        val authFeature = shikimoriFeatureFacade.scope.get<AuthFeature>()
        return AnimesAggregatorFeature(AnimesFeature(storeFactory), paginationFeature, authFeature)
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
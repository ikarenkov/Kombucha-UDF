package io.github.ikarenkov.sample.shikimori.impl.animes

import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.ReducerStoreFactory
import io.github.ikarenkov.sample.core.pagination.PaginationDataFetcher
import io.github.ikarenkov.sample.core.pagination.PaginationStore
import io.github.ikarenkov.sample.shikimori.api.shikimoriFeatureFacade
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthStore
import io.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi

internal class AnimesStoreAgregatorFactory(
    private val reducerStoreFactory: ReducerStoreFactory,
    private val animesDataFetcher: AnimesDataFetcher,
) {

    fun createStore(): Store<AnimesAggregatorStore.Msg, AnimesAggregatorStore.State, AnimesAggregatorStore.Eff> {
        val paginationFeature = PaginationStore(
            reducerStoreFactory = reducerStoreFactory,
            name = "AnimePagination",
            dataFetcher = animesDataFetcher
        )
        val authStore = shikimoriFeatureFacade.scope.get<AuthStore>()
        return AnimesAggregatorStore(AnimesStore(reducerStoreFactory), paginationFeature, authStore)
    }

    data class Anime(val id: String, val name: String)

    class AnimesDataFetcher(
        private val api: ShikimoriBackendApi
    ) : PaginationDataFetcher<Anime> {

        override suspend fun fetch(page: Int, size: Int): PaginationDataFetcher.Response<Anime> =
            api
                .animes(page, size)
                .map { anime -> Anime(anime.id.toString(), anime.name) }
                .let {
                    PaginationDataFetcher.Response<Anime>(it, null)
                }

    }

}
package io.github.ikarenkov.sample.shikimori.impl.animes

import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreFactory
import io.github.ikarenkov.sample.core.pagination.PaginationEff
import io.github.ikarenkov.sample.core.pagination.PaginationMsg
import io.github.ikarenkov.sample.core.pagination.PaginationStore
import io.github.ikarenkov.sample.shikimori.api.shikimoriFeatureFacade
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthStore
import io.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi

internal class AnimesStoreAgregatorFactory(
    private val storeFactory: StoreFactory,
    private val animesDataFetcher: AnimesDataFetcher,
) {

    fun createStore(): Store<AnimesAggregatorStore.Msg, AnimesAggregatorStore.State, AnimesAggregatorStore.Eff> {
        val paginationFeature = PaginationStore(
            storeFactory = storeFactory,
            name = "AnimePagination",
            dataFetcher = animesDataFetcher
        )
        val authStore = shikimoriFeatureFacade.scope.get<AuthStore>()
        return AnimesAggregatorStore(AnimesStore(storeFactory), paginationFeature, authStore)
    }

    data class Anime(val id: String, val name: String)

    class AnimesDataFetcher(
        private val api: ShikimoriBackendApi
    ) : io.github.ikarenkov.sample.core.pagination.PaginationEffectHandler.DataFetcher<Anime> {

        override suspend fun fetch(input: PaginationEff.Load): PaginationMsg.Inner.LoadResult<Anime> =
            api
                .animes(input.page, input.size)
                .map { animes -> animes.map { Anime(it.id.toString(), it.name) } }
                .let {
                    PaginationMsg.Inner.LoadResult<Anime>(it, input.page, input.size)
                }
    }

}
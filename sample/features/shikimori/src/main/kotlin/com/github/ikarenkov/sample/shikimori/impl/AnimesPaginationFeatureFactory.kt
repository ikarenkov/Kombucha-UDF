package com.github.ikarenkov.sample.shikimori.impl

import com.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationEffectHandler
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationFeature
import ru.ikarenkov.teamaker.store.Store
import ru.ikarenkov.teamaker.store.StoreFactory

class AnimesPaginationFeatureFactory(
    private val storeFactory: StoreFactory,
    private val animesDataFetcher: AnimesDataFetcher
) {

    fun createStore(): Store<PaginationFeature.Msg, PaginationFeature.State<Anime>, PaginationFeature.Eff> =
        PaginationFeature.create(
            storeFactory = storeFactory,
            name = "AnimePagination",
            dataFetcher = animesDataFetcher
        )

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
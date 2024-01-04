package com.github.ikarenkov.sample.shikimori.impl

import com.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationEffectHandler
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationFeature
import ru.ikarenkov.teamaker.eff_handler.adaptCast
import ru.ikarenkov.teamaker.store.Store
import ru.ikarenkov.teamaker.store.StoreFactory

class AnimesPaginationFeature(
    private val storeFactory: StoreFactory,
    private val animesDataFetcher: AnimesDataFetcher
) {

    fun createStore(): Store<PaginationFeature.Msg, PaginationFeature.State<Anime>, PaginationFeature.Eff> = storeFactory.create(
        name = "AnimePagination",
        initialState = PaginationFeature.State.Initial(),
        reducer = PaginationFeature.reducer<Anime>()::invoke,
        initEffects = PaginationFeature.Eff.Initial(),
        PaginationEffectHandler(animesDataFetcher).adaptCast()
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
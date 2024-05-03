package io.github.ikarenkov.sample.favorite.impl.aggregated

import io.github.ikarenkov.kombucha.store.ReducerStoreFactory
import io.github.ikarenkov.sample.core.pagination.PaginationDataFetcher
import io.github.ikarenkov.sample.core.pagination.PaginationStore
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteItem
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteRepository

internal class FavoritePaginationStore(
    reducerStoreFactory: ReducerStoreFactory,
    dataFetcher: DataFetcher,
) : PaginationStore<FavoriteItem>(
    name = "FavoritePagination",
    reducerStoreFactory = reducerStoreFactory,
    dataFetcher = dataFetcher
) {

    class DataFetcher(
        private val favoriteRepository: FavoriteRepository
    ) : PaginationDataFetcher<FavoriteItem> {
        override suspend fun fetch(page: Int, size: Int): PaginationDataFetcher.Response<FavoriteItem> {
            if (page <= 1) {
                return PaginationDataFetcher.Response(favoriteRepository.loadFavoriteItems(), 1)
            } else {
                error("We only have a one page.")
            }
        }

    }

}
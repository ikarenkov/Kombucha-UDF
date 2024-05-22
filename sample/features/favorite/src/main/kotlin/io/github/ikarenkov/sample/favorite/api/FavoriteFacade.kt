package io.github.ikarenkov.sample.favorite.api

import io.github.ikarenkov.kombucha.sample.core.feature.featureFacade
import io.github.ikarenkov.sample.favorite.impl.FavoriteAnalytics
import io.github.ikarenkov.sample.favorite.impl.FavoriteEffHandler
import io.github.ikarenkov.sample.favorite.impl.FavoriteScreenModel
import io.github.ikarenkov.sample.favorite.impl.FavoriteListStore
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteAggregatorStore
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteEffectHandler
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoriteInteractionStore
import io.github.ikarenkov.sample.favorite.impl.aggregated.FavoritePaginationStore
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteRepository

val favoriteSampleFacade by lazy {
    featureFacade<Any, FavoriteApi>("TeaSampleFacade") {
        scoped { FavoriteApi(get()) }
        scoped { FavoriteInteractionStore(get(), get()) }
        scoped { FavoriteAnalytics() }
        factory { FavoriteAggregatorStore(get(), get()) }
        factory { FavoriteEffectHandler(get()) }
        factory { FavoritePaginationStore(get(), get()) }
        factory { FavoritePaginationStore.DataFetcher(get()) }
        factory { FavoriteRepository() }
        factory { FavoriteListStore(get(), initialState = it.get(), get()) }
        factory { FavoriteScreenModel(get()) }
        factory { FavoriteEffHandler(get()) }
    }
}
package io.github.ikarenkov.sample.favorite.api

import io.github.ikarenkov.kombucha.sample.core.feature.featureFacade
import io.github.ikarenkov.sample.favorite.impl.FavoriteScreenModel
import io.github.ikarenkov.sample.favorite.impl.FavoriteStore
import io.github.ikarenkov.sample.favorite.impl.FavoriteEffHandler
import io.github.ikarenkov.sample.favorite.impl.data.FavoriteRepository

val favoriteSampleFacade by lazy {
    featureFacade<Any, FavoriteApi>("TeaSampleFacade") {
        scoped { FavoriteApi() }
        factory { FavoriteRepository() }
        factory { FavoriteStore(get(), get()) }
        factory { FavoriteScreenModel(get()) }
        factory { FavoriteEffHandler(get()) }
    }
}
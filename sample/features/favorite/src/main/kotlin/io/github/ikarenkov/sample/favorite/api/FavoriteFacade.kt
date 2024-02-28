package io.github.ikarenkov.sample.favorite.api

import io.github.ikarenkov.kombucha.sample.core.feature.featureFacade
import io.github.ikarenkov.sample.favorite.impl.FavScreenModel
import io.github.ikarenkov.sample.favorite.impl.FavoriteStore
import io.github.ikarenkov.sample.favorite.impl.KombuchaFavEffHandler
import io.github.ikarenkov.sample.favorite.impl.data.DemoFavRepository

val favoriteSampleFacade by lazy {
    featureFacade<Any, FavoriteApi>("TeaSampleFacade") {
        scoped { FavoriteApi() }
        factory { DemoFavRepository() }
        factory { FavoriteStore(get(), get()) }
        factory { FavScreenModel(get()) }
        factory { KombuchaFavEffHandler(get()) }
    }
}
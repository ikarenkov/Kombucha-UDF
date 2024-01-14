package com.github.ikarenkov.sample.shikimori.api

import com.github.ikarenkov.sample.shikimori.impl.AnimesPaginationFeatureFactory
import com.github.ikarenkov.sample.shikimori.impl.AnimesScreenModel
import com.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
import ru.ikarenkov.core.feature.featureFacade

val shikimoriFeatureFacade by lazy {
    featureFacade<ShikimoriDeps, com.github.ikarenkov.sample.shikimori.api.ShikimoriApi>("items") {
        scoped { com.github.ikarenkov.sample.shikimori.api.ShikimoriApi() }
        factory { ShikimoriBackendApi() }
        factory { AnimesScreenModel(get()) }
        factory { AnimesPaginationFeatureFactory(get(), get()) }
        factory { AnimesPaginationFeatureFactory.AnimesDataFetcher(get()) }
    }
}
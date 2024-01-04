package com.github.ikarenkov.sample.shikimori.api

import com.github.ikarenkov.sample.shikimori.impl.AnimesPaginationFeature
import com.github.ikarenkov.sample.shikimori.impl.AnimesScreenModel
import com.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
import ru.ikarenkov.core.feature.featureFacade

val shikimoriFeatureFacade by lazy {
    featureFacade<ShikimoriDeps, com.github.ikarenkov.sample.shikimori.api.ShikimoriApi>("items") {
        scoped { com.github.ikarenkov.sample.shikimori.api.ShikimoriApi() }
        factory { ShikimoriBackendApi() }
        factory { AnimesScreenModel(get()) }
        factory { AnimesPaginationFeature(get(), get()) }
        factory { AnimesPaginationFeature.AnimesDataFetcher(get()) }
    }
}
package com.github.ikarenkov.sample.shikimori.api

import com.github.ikarenkov.sample.shikimori.impl.AnimesScreenModel
import com.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeatureAgregatorFactory
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import com.github.ikarenkov.sample.shikimori.impl.data.AuthDataLocalStorage
import com.github.ikarenkov.sample.shikimori.impl.data.HttpClientFactory
import com.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
import io.ktor.client.HttpClient
import ru.ikarenkov.core.feature.featureFacade

val shikimoriFeatureFacade by lazy {
    featureFacade<ShikimoriDeps, ShikimoriApi>("items") {
        scoped { ShikimoriApi(get()) }
        scoped<HttpClient> { HttpClientFactory().createClient() }
        scoped { ShikimoriBackendApi(get()) }
        scoped { AuthDataLocalStorage(get()) }

        scoped { AuthFeature(get(), get()) }

        factory { AnimesScreenModel(get()) }
        factory { AnimesFeatureAgregatorFactory(get(), get(), get(), get()) }
        factory { AnimesFeatureAgregatorFactory.AnimesDataFetcher(get()) }
        factory { AuthFeature.AuthEffHandler(get(), get(), get()) }
    }
}
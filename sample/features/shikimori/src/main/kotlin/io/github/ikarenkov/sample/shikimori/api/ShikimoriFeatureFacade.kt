package io.github.ikarenkov.sample.shikimori.api

import io.github.ikarenkov.kombucha.sample.core.feature.featureFacade
import io.github.ikarenkov.sample.shikimori.impl.AnimesScreenModel
import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeatureAgregatorFactory
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import io.github.ikarenkov.sample.shikimori.impl.data.AuthDataLocalStorage
import io.github.ikarenkov.sample.shikimori.impl.data.HttpClientFactory
import io.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
import io.ktor.client.HttpClient

val shikimoriFeatureFacade by lazy {
    featureFacade<ShikimoriDeps, ShikimoriApi>("Shikimori") {
        scoped { ShikimoriApi(get()) }
        scoped<HttpClient> { HttpClientFactory().createClient() }
        scoped { ShikimoriBackendApi(get()) }
        scoped { AuthDataLocalStorage(get()) }

        scoped { AuthFeature(get(), get()) }

        factory { AnimesScreenModel(get()) }
        factory { AnimesFeatureAgregatorFactory(get(), get()) }
        factory { AnimesFeatureAgregatorFactory.AnimesDataFetcher(get()) }
        factory { AuthFeature.AuthEffHandler(get(), get(), get()) }
    }
}
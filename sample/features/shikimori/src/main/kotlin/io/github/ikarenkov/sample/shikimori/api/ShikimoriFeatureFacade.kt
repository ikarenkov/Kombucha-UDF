package io.github.ikarenkov.sample.shikimori.api

import io.github.ikarenkov.kombucha.sample.core.feature.featureFacade
import io.github.ikarenkov.sample.shikimori.impl.AnimesScreenModel
import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesStoreAgregatorFactory
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthStore
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

        scoped { AuthStore(get(), get()) }

        factory { AnimesScreenModel(get()) }
        factory { AnimesStoreAgregatorFactory(get(), get()) }
        factory { AnimesStoreAgregatorFactory.AnimesDataFetcher(get()) }
        factory { AuthFeature.AuthEffHandler(get(), get(), get()) }
    }
}
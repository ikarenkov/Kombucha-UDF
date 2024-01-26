package com.github.ikarenkov.sample.shikimori.api

import com.github.ikarenkov.kombucha.sample.core.feature.featureFacade
import com.github.ikarenkov.sample.shikimori.impl.AnimesScreenModel
import com.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeatureAgregatorFactory
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import com.github.ikarenkov.sample.shikimori.impl.data.AuthDataLocalStorage
import com.github.ikarenkov.sample.shikimori.impl.data.HttpClientFactory
import com.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
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
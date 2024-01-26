package io.github.ikarenkov.kombucha.sample.di

import android.content.Context
import io.github.ikarenkov.kombucha.sample.NavigationHolder
import io.github.ikarenkov.kombucha.sample.deps.CounterDepsImpl
import io.github.ikarenkov.kombucha.sample.deps.ShikimoryDepsImpl
import io.github.ikarenkov.kombucha.sample.kombucha.KombuchaStoreFactory
import io.github.ikarenkov.sample.shikimori.api.ShikimoriDeps
import org.koin.dsl.module
import io.github.ikarenkov.kombucha.sample.counter.api.CounterDeps
import io.github.ikarenkov.kombucha.store.StoreFactory

fun appModule(context: Context) = module {
    single<Context> { context }
    single<StoreFactory> { KombuchaStoreFactory() }

    single<NavigationHolder> { NavigationHolder() }
    single<CounterDeps> { CounterDepsImpl(get()) }
    single<ShikimoryDepsImpl> { ShikimoryDepsImpl() }
    single<ShikimoriDeps> { get<ShikimoryDepsImpl>() }
}
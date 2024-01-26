package com.github.ikarenkov.kombucha.sample.di

import android.content.Context
import com.github.ikarenkov.kombucha.sample.NavigationHolder
import com.github.ikarenkov.kombucha.sample.deps.CounterDepsImpl
import com.github.ikarenkov.kombucha.sample.deps.ShikimoryDepsImpl
import com.github.ikarenkov.kombucha.sample.kombucha.KombuchaStoreFactory
import com.github.ikarenkov.sample.shikimori.api.ShikimoriDeps
import org.koin.dsl.module
import com.github.ikarenkov.kombucha.sample.counter.api.CounterDeps
import com.github.ikarenkov.kombucha.store.StoreFactory

fun appModule(context: Context) = module {
    single<Context> { context }
    single<StoreFactory> { KombuchaStoreFactory() }

    single<NavigationHolder> { NavigationHolder() }
    single<CounterDeps> { CounterDepsImpl(get()) }
    single<ShikimoryDepsImpl> { ShikimoryDepsImpl() }
    single<ShikimoriDeps> { get<ShikimoryDepsImpl>() }
}
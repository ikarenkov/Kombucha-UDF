package com.github.ikarenkov.kombucha.sample.di

import android.content.Context
import com.github.ikarenkov.kombucha.sample.NavigationHolder
import com.github.ikarenkov.kombucha.sample.deps.CounterDepsImpl
import com.github.ikarenkov.kombucha.sample.deps.ShikimoryDepsImpl
import com.github.ikarenkov.kombucha.sample.kombucha.KombuchaStoreFactory
import com.github.ikarenkov.sample.shikimori.api.ShikimoriDeps
import org.koin.dsl.module
import ru.ikarenkov.kombucha.sample.counter.api.CounterDeps
import ru.ikarenkov.kombucha.store_legacy.StoreFactory
import ru.ikarenkov.kombucha.store_legacy.SyncStoreFactory

fun appModule(context: Context) = module {
    single<Context> { context }
    single<StoreFactory> { SyncStoreFactory() }
    single<ru.ikarenkov.kombucha.store.StoreFactory> { KombuchaStoreFactory() }

    single<NavigationHolder> { NavigationHolder() }
    single<CounterDeps> { CounterDepsImpl(get()) }
    single<ShikimoryDepsImpl> { ShikimoryDepsImpl() }
    single<ShikimoriDeps> { get<ShikimoryDepsImpl>() }
}
package com.github.ikarenkov.kombucha.sample.di

import com.github.ikarenkov.kombucha.sample.NavigationHolder
import com.github.ikarenkov.kombucha.sample.deps.CounterDepsImpl
import com.github.ikarenkov.kombucha.sample.kobucha.KombuchaStoreFactory
import org.koin.dsl.module
import ru.ikarenkov.teamaker.sample.counter.api.CounterDeps
import ru.ikarenkov.teamaker.store_legacy.StoreFactory
import ru.ikarenkov.teamaker.store_legacy.SyncStoreFactory

val appModule = module {
    single<StoreFactory> { SyncStoreFactory() }
    single<ru.ikarenkov.teamaker.store.StoreFactory> { KombuchaStoreFactory() }

    single<NavigationHolder> { NavigationHolder() }
    single<CounterDeps> { CounterDepsImpl(get()) }
}
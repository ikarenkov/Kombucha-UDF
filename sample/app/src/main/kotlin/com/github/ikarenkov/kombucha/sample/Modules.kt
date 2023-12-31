package com.github.ikarenkov.kombucha.sample

import com.github.ikarenkov.kombucha.sample.deps.CounterDepsImpl
import org.koin.dsl.module
import ru.ikarenkov.teamaker.StoreFactory
import ru.ikarenkov.teamaker.SyncStoreFactory
import ru.ikarenkov.teamaker.sample.counter.api.CounterDeps

val appModule = module {
    single<StoreFactory> { SyncStoreFactory() }
    single<NavigationHolder> { NavigationHolder() }
    single<CounterDeps> { CounterDepsImpl(get()) }
}
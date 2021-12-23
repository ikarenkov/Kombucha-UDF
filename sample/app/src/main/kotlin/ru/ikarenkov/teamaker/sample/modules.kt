package ru.ikarenkov.teamaker.sample

import org.koin.dsl.module
import ru.ikarenkov.teamaker.StoreFactory
import ru.ikarenkov.teamaker.SyncStoreFactory
import ru.ikarenkov.teamaker.sample.root.RootStoreFactory

val appModule = module {
    single<StoreFactory> { SyncStoreFactory() }
    factory { RootStoreFactory(get()) }
}
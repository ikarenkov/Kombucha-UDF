package ru.ikarenkov.teamaker.sample

import com.github.terrakok.modo.LogReducer
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.compose.AppReducer
import org.koin.dsl.module
import ru.ikarenkov.teamaker.StoreFactory
import ru.ikarenkov.teamaker.SyncStoreFactory
import ru.ikarenkov.teamaker.sample.counter.api.CounterDeps
import ru.ikarenkov.teamaker.sample.deps.CounterDepsImpl

val appModule = module {
    single<StoreFactory> { SyncStoreFactory() }
    single<Modo> { Modo(LogReducer(AppReducer(get()))) }
    single<CounterDeps> { CounterDepsImpl(get()) }
}
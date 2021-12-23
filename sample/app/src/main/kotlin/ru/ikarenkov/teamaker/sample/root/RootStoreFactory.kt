package ru.ikarenkov.teamaker.sample.root

import org.koin.core.Koin
import ru.ikarenkov.teamaker.StoreFactory

internal class RootStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create() = storeFactory.create(
        "ROOT",
        State(0),
        rootReducer::invoke
    )

}

fun Koin.createRootStore() = get<RootStoreFactory>().create()
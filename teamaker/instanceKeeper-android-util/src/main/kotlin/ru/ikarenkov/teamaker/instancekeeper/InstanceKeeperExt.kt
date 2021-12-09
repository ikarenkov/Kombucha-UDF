package ru.ikarenkov.teamaker.instancekeeper

import androidx.fragment.app.Fragment
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.instancekeeper.instanceKeeper
import ru.ikarenkov.teamaker.Feature

fun <T : Feature<*, *, *>> Fragment.getStore(key: Any, factory: () -> T): T = instanceKeeper().getStore(key, factory)

inline fun <reified T : Feature<*, *, *>> Fragment.getStore(noinline factory: () -> T): T = instanceKeeper().getStore(factory)

fun <T : Feature<*, *, *>> InstanceKeeper.getStore(key: Any, factory: () -> T): T =
        getOrCreate(key = key) {
            StoreInstance(factory())
        }.store

inline fun <reified T : Feature<*, *, *>> InstanceKeeper.getStore(noinline factory: () -> T): T =
        getStore(key = T::class, factory = factory)

private class StoreInstance<out T : Feature<*, *, *>>(
        val store: T
) : InstanceKeeper.Instance {

    override fun onDestroy() {
        store.cancel()
    }

}

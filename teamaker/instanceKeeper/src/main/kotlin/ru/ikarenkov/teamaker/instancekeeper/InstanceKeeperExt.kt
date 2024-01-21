package ru.ikarenkov.teamaker.instancekeeper

import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.instancekeeper.instanceKeeper
import ru.ikarenkov.teamaker.store.Store

fun <T : Store<*, *, *>> ViewModelStoreOwner.getStore(key: Any, factory: () -> T): T = instanceKeeper().getStore(key, factory)

inline fun <reified T : Store<*, *, *>> ViewModelStoreOwner.getStore(noinline factory: () -> T): T = instanceKeeper().getStore(factory)

fun <T : Store<*, *, *>> InstanceKeeper.getStore(key: Any, factory: () -> T): T =
    getOrCreate(key = key) {
        StoreInstance(factory())
    }.store

inline fun <reified T : Store<*, *, *>> InstanceKeeper.getStore(noinline factory: () -> T): T =
    getStore(key = T::class, factory = factory)

private class StoreInstance<out T : Store<*, *, *>>(
    val store: T
) : InstanceKeeper.Instance {

    override fun onDestroy() {
        store.cancel()
    }

}

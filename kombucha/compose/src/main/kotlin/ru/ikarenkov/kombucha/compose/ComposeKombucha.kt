package ru.ikarenkov.kombucha.compose

import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.instancekeeper.instanceKeeper
import kotlinx.coroutines.flow.StateFlow
import ru.ikarenkov.kombucha.Cancelable
import ru.ikarenkov.kombucha.store.Store

/**
 * This interface provides Ui related part of TEA for compose.
 * @param UiMsg - messages that can be dispatched to store
 * @param Model - model of screen that can be observed
 * @param UiEff - single event effects that can be consumed by compose screen (WIP)
 */
interface ComposeKombucha<UiMsg : Any, Model : Any, UiEff : Any> : Cancelable {

    fun accept(msg: UiMsg)

    val state: StateFlow<Model>

}

open class ComposeKombuchaImpl<Msg : Any, UiMsg : Msg, Model : Any, Eff : Any, UiEff : Any>(
    protected val store: Store<Msg, Model, Eff>,
) : ComposeKombucha<UiMsg, Model, UiEff> {

    override var state: StateFlow<Model> = store.state

    override fun accept(msg: UiMsg) {
        store.accept(msg)
    }

    override fun cancel() {
        store.cancel()
    }

}

class ComposeKombuchaInstance<UiMsg : Any, Model : Any, UiEff : Any>(
    composeKombucha: ComposeKombucha<UiMsg, Model, UiEff>
) : ComposeKombucha<UiMsg, Model, UiEff> by composeKombucha, InstanceKeeper.Instance {
    override fun onDestroy() {
        cancel()
    }
}

fun <UiMsg : Any, Model : Any, UiEff : Any> ComposeKombucha<UiMsg, Model, UiEff>.adaptInstanceKeeper(): ComposeKombuchaInstance<UiMsg, Model, UiEff> =
    ComposeKombuchaInstance<UiMsg, Model, UiEff>(this)

/**
 * Return [ComposeKombucha] instance from [ViewModelStoreOwner.getViewModelStore] if instance already exists,
 * or creates and put new instance to [ViewModelStoreOwner]
 */
fun <Msg : Any, UiMsg : Msg, Model : Any, Eff : Any, UiEff : Any> ViewModelStoreOwner.brewComposeTea(
    key: Any = ComposeKombuchaInstance::class,
    storeFactory: () -> Store<Msg, Model, Eff>
): ComposeKombucha<UiMsg, Model, UiEff> = instanceKeeper().getOrCreate(key) {
    ComposeKombuchaImpl<Msg, UiMsg, Model, Eff, UiEff>(storeFactory()).adaptInstanceKeeper()
}

// TODO: do we really need this? Maybe it will be better to write specific extension or delegate for specific case,
//  f.e. fragment of Modo screen, to exclude case, whoth we can forget to cancel subscription.
//  In the other hand we steel can use it to write extension fun.
/**
 * Just map tea store to [ComposeTeaInstanceKeeperImpl], allows to dispatch messages and observe state.
 * !!! You mast manually call [ComposeTeaInstanceKeeperImpl.onDestroy] to cancel feature
 */
fun <Msg : Any, UiMsg : Msg, Model : Any, Eff : Any, UiEff : Any> Store<Msg, Model, Eff>.asCompose() =
    ComposeKombuchaImpl<Msg, UiMsg, Model, Eff, UiEff>(this)
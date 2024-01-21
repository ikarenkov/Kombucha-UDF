package ru.ikarenkov.kombucha.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.instancekeeper.instanceKeeper
import ru.ikarenkov.kombucha.Cancelable
import ru.ikarenkov.kombucha.store_legacy.Store

/**
 * This interface provides Ui related part of TEA for compose.
 * @param UiMsg - messages that can be dispatched to store
 * @param Model - model of screen that can be observed
 * @param UiEff - single event effects that can be consumed by compose screen (WIP)
 */
interface ComposeTea<UiMsg : Any, Model : Any, UiEff : Any> : Cancelable {

    fun dispatch(msg: UiMsg)

    val state: Model

}

open class ComposeTeaImpl<Msg : Any, UiMsg : Msg, Model : Any, Eff : Any, UiEff : Any>(
    protected val tea: Store<Msg, Model, Eff>,
) : ComposeTea<UiMsg, Model, UiEff> {

    override var state: Model by mutableStateOf(tea.currentState)

    private val stateDisposable = tea.listenState {
        state = it
    }

    override fun dispatch(msg: UiMsg) {
        tea.dispatch(msg)
    }

    override fun cancel() {
        stateDisposable.cancel()
        tea.cancel()
    }

}

class ComposeTeaInstance<UiMsg : Any, Model : Any, UiEff : Any>(
    composeTea: ComposeTea<UiMsg, Model, UiEff>
) : ComposeTea<UiMsg, Model, UiEff> by composeTea, InstanceKeeper.Instance {
    override fun onDestroy() {
        cancel()
    }
}

fun <UiMsg : Any, Model : Any, UiEff : Any> ComposeTea<UiMsg, Model, UiEff>.adaptInstanceKeeper(): ComposeTeaInstance<UiMsg, Model, UiEff> =
    ComposeTeaInstance<UiMsg, Model, UiEff>(this)

/**
 * Return [ComposeTea] instance from [ViewModelStoreOwner.getViewModelStore] if instance already exists,
 * or creates and put new instance to [ViewModelStoreOwner]
 */
fun <Msg : Any, UiMsg : Msg, Model : Any, Eff : Any, UiEff : Any> ViewModelStoreOwner.brewComposeTea(
    key: Any = ComposeTeaInstance::class,
    storeFactory: () -> Store<Msg, Model, Eff>
): ComposeTea<UiMsg, Model, UiEff> = instanceKeeper().getOrCreate(key) {
    ComposeTeaImpl<Msg, UiMsg, Model, Eff, UiEff>(storeFactory()).adaptInstanceKeeper()
}

// TODO: do we really need this? Maybe it will be better to write specific extension or delegate for specific case,
//  f.e. fragment of Modo screen, to exclude case, whoth we can forget to cancel subscription.
//  In the other hand we steel can use it to write extension fun.
/**
 * Just map tea store to [ComposeTeaInstanceKeeperImpl], allows to dispatch messages and observe state.
 * !!! You mast manually call [ComposeTeaInstanceKeeperImpl.onDestroy] to cancel feature
 */
fun <Msg : Any, UiMsg : Msg, Model : Any, Eff : Any, UiEff : Any> Store<Msg, Model, Eff>.asCompose() =
    ComposeTeaImpl<Msg, UiMsg, Model, Eff, UiEff>(this)
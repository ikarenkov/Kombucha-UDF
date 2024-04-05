package io.github.ikarenkov.sample.ui.impl

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.eff_handler.adaptCast
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.ReducerStoreFactory
import io.github.ikarenkov.sample.ui.api.UiSampleDeps
import io.github.ikarenkov.sample.ui.impl.CachingUiEffectsFeature.Eff
import io.github.ikarenkov.sample.ui.impl.CachingUiEffectsFeature.Msg
import io.github.ikarenkov.sample.ui.impl.CachingUiEffectsFeature.State
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

internal class CachingUiEffectsStore(
    reducerStoreFactory: ReducerStoreFactory,
    updatedEffHandler: UpdatesEffectHandler,
    navigationEffHandler: NavigationEffHandler
) : Store<Msg, State, Eff> by reducerStoreFactory.create(
    name = "CachingUiEffectsFeature",
    initialState = State(emptyList()),
    reducer = CachingUiEffectsFeature.reducer,
    initialEffects = setOf(Eff.Int.ObserveUpdates),
    effectHandlers = arrayOf(
        updatedEffHandler.adaptCast(),
        navigationEffHandler.adaptCast()
    )
)

internal object CachingUiEffectsFeature {

    data class State(
        val itemsIds: List<String>
    )

    sealed interface Msg {
        sealed interface Int : Msg {

            data class OnNewElement(val id: String) : Int

        }

        sealed interface Ext : Msg {
            data class ItemClick(val id: String) : Ext
        }
    }

    sealed interface Eff {
        sealed interface Int : Eff {

            data object ObserveUpdates : Int
            data class OpenDetails(val id: String) : Int

        }

        sealed interface Ext : Eff {

            data class OnNewElement(val id: String) : Ext

        }

    }

    internal val reducer = dslReducer<Msg, State, Eff> { msg ->
        when (msg) {
            is Msg.Int.OnNewElement -> {
                state { copy(itemsIds = itemsIds + msg.id) }
                eff(Eff.Ext.OnNewElement(msg.id))
            }
            is Msg.Ext.ItemClick -> eff(Eff.Int.OpenDetails(msg.id))
        }
    }

}

internal class UpdatesEffectHandler : EffectHandler<Eff.Int.ObserveUpdates, Msg.Int.OnNewElement> {

    override fun handleEff(eff: Eff.Int.ObserveUpdates): Flow<Msg.Int.OnNewElement> = flow {
        while (currentCoroutineContext().isActive) {
            delay(5.seconds)
            emit(Msg.Int.OnNewElement(UUID.randomUUID().toString()))
        }
    }

}

internal class NavigationEffHandler(
    private val deps: UiSampleDeps
) : EffectHandler<Eff.Int.OpenDetails, Nothing> {

    override fun handleEff(eff: Eff.Int.OpenDetails): Flow<Nothing> = flow {
        deps.openDetailsScreen(eff.id)
    }

}
package io.github.ikarenkov.kombucha.aggregator

import io.github.ikarenkov.kombucha.store.Store
import kotlinx.coroutines.flow.Flow

suspend fun <T, Msg : Any> bindFlowToMsg(
    fromFlow: Flow<T>,
    toStore: Store<Msg, *, *>,
    transform: (T) -> Msg?
) {
    fromFlow.collect { eff ->
        transform(eff)?.let { msg -> toStore.accept(msg) }
    }
}

suspend fun <Eff : Any, Msg : Any> bindEffToMsg(
    storeEff: Store<*, *, Eff>,
    storeMsg: Store<Msg, *, *>,
    transform: (Eff) -> Msg?
) {
    bindFlowToMsg(storeEff.effects, storeMsg, transform)
}

suspend fun <State : Any, Msg : Any> bindStateToMsg(
    storeEff: Store<*, State, *>,
    storeMsg: Store<Msg, *, *>,
    transform: (State) -> Msg?
) {
    bindFlowToMsg(storeEff.state, storeMsg, transform)
}
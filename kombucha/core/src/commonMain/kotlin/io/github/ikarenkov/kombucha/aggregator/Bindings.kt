package io.github.ikarenkov.kombucha.aggregator

import io.github.ikarenkov.kombucha.store.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <Eff : Any, Msg : Any> bindEffToMsg(
    coroutineScope: CoroutineScope,
    storeEff: Store<*, *, Eff>,
    storeMsg: Store<Msg, *, *>,
    transform: (Eff) -> Msg?
) {
    coroutineScope.launch {
        storeEff.effects.collect { eff ->
            transform(eff)?.let { msg -> storeMsg.accept(msg) }
        }
    }
}

fun <State : Any, Msg : Any> bindStateToMsg(
    coroutineScope: CoroutineScope,
    storeEff: Store<*, State, *>,
    storeMsg: Store<Msg, *, *>,
    transform: (State) -> Msg?
) {
    coroutineScope.launch {
        storeEff.state.collect { eff ->
            transform(eff)?.let { msg -> storeMsg.accept(msg) }
        }
    }
}
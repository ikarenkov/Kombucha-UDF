package io.github.ikarenkov.kombucha.sample.modo_kombucha

import com.github.terrakok.modo.model.ScreenModel
import io.github.ikarenkov.kombucha.store.Store

class ModoKombuchaScreenModel<UiMsg : Any, UiState : Any, UiEff : Any>(
    val store: Store<UiMsg, UiState, UiEff>
) : ScreenModel {

    override fun onDispose() {
        store.cancel()
    }

}
package io.github.ikarenkov.kombucha.sample.modo_kombucha

import androidx.compose.runtime.Composable
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.model.ScreenModel
import com.github.terrakok.modo.model.rememberScreenModel
import io.github.ikarenkov.kombucha.store.Store

private class ModoKombuchaScreenModel<UiMsg : Any, UiState : Any, UiEff : Any>(
    val store: Store<UiMsg, UiState, UiEff>
) : ScreenModel {

    override fun onDispose() {
        store.close()
    }

}

@Composable
fun <Msg : Any, State : Any, Eff : Any> Screen.rememberKombuchaStore(
    createStore: () -> Store<Msg, State, Eff>
): Store<Msg, State, Eff> = rememberScreenModel {
    ModoKombuchaScreenModel(createStore())
}.store
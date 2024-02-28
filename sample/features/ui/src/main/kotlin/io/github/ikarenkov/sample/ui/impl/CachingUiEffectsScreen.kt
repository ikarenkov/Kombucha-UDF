package io.github.ikarenkov.sample.ui.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import io.github.ikarenkov.kombucha.sample.modo_kombucha.rememberKombuchaStore
import io.github.ikarenkov.kombucha.ui.uiBuilder
import io.github.ikarenkov.sample.ui.R
import io.github.ikarenkov.sample.ui.api.uiSampleFeatureFacade
import io.github.ikarenkov.sample.ui.impl.CachingUiEffectsFeature.Eff
import io.github.ikarenkov.sample.ui.impl.CachingUiEffectsFeature.Msg
import kotlinx.parcelize.Parcelize

@Parcelize
internal class CachingUiEffectsScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content() {
        val resources = LocalContext.current.resources
        val store = rememberKombuchaStore {
            val store = uiSampleFeatureFacade.scope.get<CachingUiEffectsStore>()
            store.uiBuilder().using<Msg.Ext, UiState, Eff.Ext> { state ->
                UiState(state.itemsIds.map { resources.getString(R.string.item_title, it) })
            }
        }
        val state by store.state.collectAsState()
        val scaffoldState = rememberScaffoldState()
        LaunchedEffect(key1 = store) {
            store.effects.collect { eff ->
                when (eff) {
                    is Eff.Ext.OnNewElement -> {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = "On new element -> ${eff.id}",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
        Scaffold(scaffoldState = scaffoldState) { padings ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padings)
                    .padding(horizontal = 8.dp),
                contentPadding = WindowInsets.statusBars.asPaddingValues()
            ) {
                items(state.items) { item ->
                    ItemCard(
                        item = item,
                        onClick = { store.accept(Msg.Ext.ItemClick(item)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    internal data class UiState(
        val items: List<String>
    )

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ItemCard(item: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier, onClick = onClick) {
        Box(Modifier.padding(16.dp)) {
            Text(text = item)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ItemCard(item = "title of the card", onClick = {})
}
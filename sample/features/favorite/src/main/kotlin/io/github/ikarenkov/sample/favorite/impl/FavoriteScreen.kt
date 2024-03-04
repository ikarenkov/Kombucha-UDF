package io.github.ikarenkov.sample.favorite.impl

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.model.ScreenModel
import io.github.ikarenkov.kombucha.sample.modo_kombucha.rememberKombuchaStore
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.ui.uiBuilder
import io.github.ikarenkov.sample.favorite.R
import io.github.ikarenkov.sample.favorite.api.favoriteSampleFacade
import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature.Msg
import io.github.ikarenkov.sample.favorite.impl.core.LCE
import io.github.ikarenkov.sample.favorite.impl.ui.DemoFavUiConverter
import io.github.ikarenkov.sample.favorite.impl.ui.DemoFavUiState
import io.github.ikarenkov.sample.favorite.impl.ui.FavoriteListItem
import io.github.ikarenkov.sample.favorite.impl.ui.FavoriteListItemContent
import kotlinx.parcelize.Parcelize
import java.io.IOException

@Parcelize
class FavoriteScreen(
    override val screenKey: ScreenKey = generateScreenKey(),
) : Screen {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val store = rememberKombuchaStore {
            val store = favoriteSampleFacade.scope.get<FavoriteStore>()
            store
                .uiBuilder()
                .using<Msg.Outer, DemoFavUiState, FavoriteFeature.Eff.Outer>(
                    uiStateConverter = { state -> DemoFavUiConverter.convert(state) },
                )
        }
        val resources = LocalContext.current.resources
        val scaffoldState = rememberScaffoldState()
        LaunchedEffect(key1 = store) {
            store.effects.collect { eff ->
                val text = when (eff) {
                    is FavoriteFeature.Eff.Outer.ItemAdded ->
                        resources.getString(R.string.favorite_item_added, eff.id)
                    is FavoriteFeature.Eff.Outer.ItemRemoved ->
                        resources.getString(R.string.favorite_item_removed, eff.id)
                    is FavoriteFeature.Eff.Outer.ItemClick ->
                        resources.getString(R.string.favorite_item_clicked, eff.id)
                    is FavoriteFeature.Eff.Outer.ItemRemoveError ->
                        resources.getString(R.string.favorite_item_remove_error, eff.id)
                }
                scaffoldState.snackbarHostState.showSnackbar(text)
            }
        }

        FavoriteScreenContent(
            scaffoldState = scaffoldState,
            state = store.state.collectAsState().value,
            removeFavoriteClick = { store.accept(Msg.Outer.RemoveFavorite(it)) },
            itemClick = { store.accept(Msg.Outer.ItemClick(it)) },
            retryLoad = { store.accept(Msg.Outer.RetryLoad) }
        )
    }

}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun FavoriteScreenContent(
    scaffoldState: ScaffoldState,
    state: DemoFavUiState,
    removeFavoriteClick: (id: String) -> Unit,
    itemClick: (id: String) -> Unit,
    retryLoad: () -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                contentPadding = WindowInsets.statusBars.asPaddingValues(),
                content = {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProvideTextStyle(value = MaterialTheme.typography.h6) {
                            CompositionLocalProvider(
                                LocalContentAlpha provides ContentAlpha.high
                            ) {
                                Text("Favorite")
                            }
                        }
                    }
                }
            )
        }
    ) {
        val items: List<FavoriteListItem> by remember(state) { derivedStateOf { state.listCells.data.orEmpty() } }
        val showError: Boolean by remember(state) {
            derivedStateOf {
                state.listCells.data.isNullOrEmpty() && state.listCells is LCE.Error
            }
        }
        if (showError) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(80.dp),
                    painter = rememberVectorPainter(image = Icons.Outlined.Warning),
                    contentDescription = "Error"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Error of loading")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = retryLoad) {
                    Text(text = "Retry")
                }
            }
        } else {
            FavoriteList(
                items = items,
                removeFavoriteClick = removeFavoriteClick,
                itemClick = itemClick
            )
        }
    }
}

internal class FavoriteScreenModel(
    store: FavoriteStore
) : ScreenModel {

    val uiStore: Store<Msg.Outer, DemoFavUiState, FavoriteFeature.Eff.Outer> = store.uiBuilder()
        .using<Msg.Outer, DemoFavUiState, FavoriteFeature.Eff.Outer>(
            uiStateConverter = { state -> DemoFavUiConverter.convert(state) },
        )

    override fun onDispose() {
        super.onDispose()
        uiStore.close()
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FavoriteList(
    items: List<FavoriteListItem>,
    removeFavoriteClick: (id: String) -> Unit,
    itemClick: (id: String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp)
    ) {
        items(
            items = items,
            key = { item ->
                when (item) {
                    is FavoriteListItem.Skeleton -> item
                    is FavoriteListItem.Item -> item.id
                }
            }
        ) { item ->
            FavoriteListItemContent(
                item = item as? FavoriteListItem.Item,
                removeFavoriteClick = (item as? FavoriteListItem.Item)?.let {
                    { removeFavoriteClick(item.id) }
                },
                itemClick = (item as? FavoriteListItem.Item)?.let {
                    { itemClick(item.id) }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
@Suppress("MagicNumber")
private fun PreviewFavoriteScreenContent() {
    MaterialTheme {
        FavoriteScreenContent(
            scaffoldState = rememberScaffoldState(),
            state = DemoFavUiState(
                LCE.Data(
                    List(5) {
                        FavoriteListItem.Item(it.toString(), "Item: $it")
                    }
                )
            ),
            removeFavoriteClick = {},
            itemClick = {},
            retryLoad = {}
        )
    }
}

@Preview
@Composable
@Suppress("MagicNumber")
private fun PreviewFavoriteError() {
    MaterialTheme {
        FavoriteScreenContent(
            scaffoldState = rememberScaffoldState(),
            state = DemoFavUiState(
                LCE.Error(IOException())
            ),
            removeFavoriteClick = {},
            itemClick = {},
            retryLoad = {}
        )
    }
}
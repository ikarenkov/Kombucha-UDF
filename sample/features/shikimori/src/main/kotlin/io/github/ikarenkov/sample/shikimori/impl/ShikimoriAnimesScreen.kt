package io.github.ikarenkov.sample.shikimori.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.model.ScreenModel
import com.github.terrakok.modo.model.rememberScreenModel
import io.github.ikarenkov.sample.shikimori.api.shikimoriFeatureFacade
import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesAggregatorFeature
import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeature
import io.github.ikarenkov.sample.shikimori.impl.animes.AnimesFeatureAgregatorFactory
import io.github.ikarenkov.sample.shikimori.impl.pagination.PaginationFeature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

@Parcelize
internal class AnimesScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content() {
        val model = rememberScreenModel {
            shikimoriFeatureFacade.scope.get<AnimesScreenModel>()
        }
        AnimesScreenContent(model)
    }

}

internal class AnimesScreenModel(
    private val animesFeatureAgregatorFactory: AnimesFeatureAgregatorFactory
) : ScreenModel {

    val store = animesFeatureAgregatorFactory.createStore()

    override fun onDispose() {
        store.cancel()
    }

}

@Composable
private fun AnimesScreenContent(model: AnimesScreenModel) {
    val state by model.store.state.collectAsState()
    val animesState by remember { derivedStateOf { state.animesState } }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { model.store.accept(AnimesAggregatorFeature.Msg.Animes(AnimesFeature.Msg.OnAuthClick)) }) {
                when (animesState) {
                    is AnimesFeature.State.AuthInProgress -> {
                        CircularProgressIndicator()
                    }
                    AnimesFeature.State.Authorized -> {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Filled.AccountCircle),
                            contentDescription = "Auth"
                        )
                    }
                    AnimesFeature.State.NotAuthorized -> {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Outlined.AccountCircle),
                            contentDescription = "Auth"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        val paginationState by remember { derivedStateOf { state.paginationState } }
        val lazyListState = rememberLazyListState()
        LaunchedEffect(key1 = Unit) {
            snapshotFlow { lazyListState.layoutInfo }
                .map {
                    it.visibleItemsInfo.isNotEmpty() &&
                            paginationState.items.size - 1 <= it.visibleItemsInfo.last().index
                }
                .collect { needLoadMore ->
                    withContext(Dispatchers.IO) {
                        if (needLoadMore) {
                            model.store.accept(AnimesAggregatorFeature.Msg.Pagination(PaginationFeature.Msg.LoadNext))
                        }
                    }
                }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = WindowInsets.systemBars.asPaddingValues()
        ) {
            items(items = paginationState.items) { anime ->
                ItemAnime(anime)
            }
            if (paginationState.items.isNotEmpty()) {
                when (paginationState.nextPageLoadingState) {
                    PaginationFeature.State.PageLoadingState.Loading -> item {
                        ItemLoading()
                    }
                    is PaginationFeature.State.PageLoadingState.Error -> item {
                        ItemError(model)
                    }
                    PaginationFeature.State.PageLoadingState.Idle -> {}
                }
            }
        }
        if (paginationState.items.isEmpty()) {
            EmptyStateContent(paginationState, model)
        }
    }
}

@Composable
private fun EmptyStateContent(
    paginationState: PaginationFeature.State<AnimesFeatureAgregatorFactory.Anime>,
    model: AnimesScreenModel
) {
    if (paginationState.nextPageLoadingState is PaginationFeature.State.PageLoadingState.Error) {
        Button(onClick = { model.store.accept(AnimesAggregatorFeature.Msg.Pagination(PaginationFeature.Msg.RetryLoadNext)) }) {
            Text(text = "Error, try again")
        }
    } else {
        CircularProgressIndicator()
    }
}

@Composable
private fun ItemError(model: AnimesScreenModel) {
    Card(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Box(Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    model.store.accept(AnimesAggregatorFeature.Msg.Pagination(PaginationFeature.Msg.RetryLoadNext))
                }
            ) {
                Text(text = "Error, try again")
            }
        }
    }
}

@Composable
private fun ItemLoading() {
    Card(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Box(Modifier.fillMaxWidth()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun ItemAnime(anime: AnimesFeatureAgregatorFactory.Anime) {
    Box(Modifier.padding(vertical = 8.dp)) {
        Card(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = anime.name,
                modifier = Modifier
                    .padding(16.dp)
                    .height(60.dp)
            )
        }
    }
}
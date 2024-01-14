package com.github.ikarenkov.sample.shikimori.impl

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
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ikarenkov.sample.shikimori.api.shikimoriFeatureFacade
import com.github.ikarenkov.sample.shikimori.impl.pagination.PaginationFeature
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.model.ScreenModel
import com.github.terrakok.modo.model.rememberScreenModel
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
        Scaffold { paddingValues ->
            val state by model.store.state.collectAsState()
            val lazyListState = rememberLazyListState()
            LaunchedEffect(key1 = Unit) {
                snapshotFlow { lazyListState.layoutInfo }
                    .map {
                        it.visibleItemsInfo.isNotEmpty() &&
                            state.items.size - 1 <= it.visibleItemsInfo.last().index
                    }
                    .collect { needLoadMore ->
                        withContext(Dispatchers.IO) {
                            if (needLoadMore) {
                                model.store.dispatch(PaginationFeature.Msg.LoadNext)
                            }
                        }
                    }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                contentPadding = WindowInsets.systemBars.asPaddingValues()
            ) {
                items(items = state.items) { anime ->
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
                if (state.items.isNotEmpty()) {
                    when (state.nextPageLoadingState) {
                        PaginationFeature.State.PageLoadingState.Loading -> item {
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
                        is PaginationFeature.State.PageLoadingState.Error -> item {
                            Card(
                                Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                Box(Modifier.fillMaxWidth()) {
                                    Button(onClick = { model.store.dispatch(PaginationFeature.Msg.RetryLoadNext) }) {
                                        Text(text = "Error, try again")
                                    }
                                }
                            }
                        }

                        PaginationFeature.State.PageLoadingState.Idle -> {}
                    }
                }
            }
            if (state.items.isEmpty()) {
                if (state.nextPageLoadingState is PaginationFeature.State.PageLoadingState.Error) {
                    Button(onClick = { model.store.dispatch(PaginationFeature.Msg.RetryLoadNext) }) {
                        Text(text = "Error, try again")
                    }
                } else {
                    CircularProgressIndicator()
                }
            }
        }

    }

}

internal class AnimesScreenModel(
    private val animesPaginationFeatureFactory: AnimesPaginationFeatureFactory
) : ScreenModel {

    val store = animesPaginationFeatureFactory.createStore()

    override fun onDispose() {
        store.cancel()
    }

}


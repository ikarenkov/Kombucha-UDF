package io.github.ikarenkov.sample.favorite.impl.aggregated

import android.annotation.SuppressLint
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.model.ScreenModel
import com.github.terrakok.modo.model.rememberScreenModel
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.ui.uiBuilder
import io.github.ikarenkov.sample.core.pagination.PaginationMsg
import io.github.ikarenkov.sample.favorite.R
import io.github.ikarenkov.sample.favorite.api.favoriteSampleFacade
import io.github.ikarenkov.sample.favorite.impl.FavoriteScreenContent
import io.github.ikarenkov.sample.favorite.impl.ui.FavoriteUiConverter
import io.github.ikarenkov.sample.favorite.impl.ui.FavoriteUiState
import kotlinx.parcelize.Parcelize

@Parcelize
class FavoriteAggregatedScreen(
    override val screenKey: ScreenKey = generateScreenKey(),
) : Screen {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        // POC restoring state after process death (PD)
//        var state: FavoriteFeature.State by rememberSaveable {
//            mutableStateOf(FavoriteFeature.State(LCE.Loading()))
//        }
        val screenModel = rememberScreenModel {
            val store = favoriteSampleFacade.scope.get<FavoriteAggregatorStore>()
            FavoriteScreenModel(store)
        }
//        LaunchedEffect(key1 = screenModel) {
//            screenModel.store.state.collect {
//                state = it
//            }
//        }
        val uiStore: Store<FavoriteAggregatedFeature.Msg, FavoriteUiState, FavoriteAggregatedFeature.Eff> = screenModel.uiStore
        val resources = LocalContext.current.resources
        val scaffoldState = rememberScaffoldState()
        LaunchedEffect(key1 = uiStore) {
            uiStore.effects.collect { eff ->
                val text: String? = when (val eff = (eff as FavoriteAggregatedFeature.Eff.FavoriteInteraction).eff) {
                    is FavoriteInteractionFeature.Eff.Outer.ItemClick ->
                        resources.getString(R.string.favorite_item_clicked, eff.id)
                    is FavoriteInteractionFeature.Eff.Outer.ItemUpdate.Finished -> {
                        if (eff.item.isFavorite) {
                            resources.getString(R.string.favorite_item_added, eff.item.id)
                        } else {
                            resources.getString(R.string.favorite_item_removed, eff.item.id)
                        }
                    }
                    is FavoriteInteractionFeature.Eff.Outer.ItemUpdate.Error ->
                        resources.getString(R.string.favorite_item_remove_error, eff.item.id)
                    is FavoriteInteractionFeature.Eff.Outer.ItemUpdate.Started -> null
                }
                if (text != null) {
                    scaffoldState.snackbarHostState.showSnackbar(text)
                }
            }
        }

        FavoriteScreenContent(
            scaffoldState = scaffoldState,
            state = uiStore.state.collectAsState().value,
            removeFavoriteClick = {
                uiStore.accept(
                    FavoriteAggregatedFeature.Msg.Msg(
                        FavoriteInteractionFeature.Msg.Outer.UpdateFavorite(
                            it,
                            false
                        )
                    )
                )
            },
            itemClick = { uiStore.accept(FavoriteAggregatedFeature.Msg.Msg(FavoriteInteractionFeature.Msg.Outer.ItemClick(it))) },
            retryLoad = { uiStore.accept(FavoriteAggregatedFeature.Msg.Msg(PaginationMsg.Outer.RetryLoadNext)) }
        )
    }

}

internal class FavoriteScreenModel(
    val store: FavoriteAggregatorStore
) : ScreenModel {

    val uiStore: Store<FavoriteAggregatedFeature.Msg, FavoriteUiState, FavoriteAggregatedFeature.Eff> =
        store.uiBuilder()
            .using<FavoriteAggregatedFeature.Msg, FavoriteUiState, FavoriteAggregatedFeature.Eff>(
                uiStateConverter = { state -> FavoriteUiConverter.convert(state) },
            )

    val uiStoreVerbose: Store<FavoriteAggregatedFeature.Msg, FavoriteUiState, FavoriteInteractionFeature.Eff.Outer> =
        store.uiBuilder()
            .using<FavoriteAggregatedFeature.Msg, FavoriteUiState, FavoriteInteractionFeature.Eff.Outer>(
                uiStateConverter = { state -> FavoriteUiConverter.convert(state) },
                uiMsgToMsgConverter = { it as FavoriteAggregatedFeature.Msg },
                uiEffConverter = { (it as? FavoriteAggregatedFeature.Eff.FavoriteInteraction)?.eff }
            )

    override fun onDispose() {
        super.onDispose()
        uiStore.close()
    }

}
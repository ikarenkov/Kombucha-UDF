package io.github.ikarenkov.sample.favorite.impl.ui

import androidx.compose.runtime.Immutable
import io.github.ikarenkov.sample.favorite.impl.core.LCE

@Immutable
internal data class FavoriteUiState(
    val listCells: LCE<List<FavoriteListItem>>,
)

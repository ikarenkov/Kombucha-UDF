package io.github.ikarenkov.sample.shikimori.api

import android.content.Intent
import kotlinx.coroutines.flow.Flow

interface ShikimoriDeps {

    fun onFavoriteClick(itemId: String)

    fun intents(): Flow<Intent>

}
package com.github.ikarenkov.kombucha.sample.deps

import android.content.Intent
import com.github.ikarenkov.sample.shikimori.api.ShikimoriDeps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class ShikimoryDepsImpl : ShikimoriDeps {

    internal val intentsSharedFlow = MutableSharedFlow<Intent>()

    override fun onFavoriteClick(itemId: String) {
        TODO("Not yet implemented")
    }

    override fun intents(): Flow<Intent> = intentsSharedFlow
}
package io.github.ikarenkov.sample.shikimori.api

import android.content.Intent
import com.github.terrakok.modo.Screen
import io.github.ikarenkov.sample.shikimori.impl.AnimesScreen
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthStore

class ShikimoriApi internal constructor(
    private val authStore: AuthStore
) {

    fun createScreen(): Screen = AnimesScreen()

    fun onIntentResult(intent: Intent) {
        authStore.accept(AuthFeature.Msg.OAuthResult(intent.data.toString()))
    }
}
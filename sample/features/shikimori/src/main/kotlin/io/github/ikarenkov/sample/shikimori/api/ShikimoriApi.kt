package io.github.ikarenkov.sample.shikimori.api

import android.content.Intent
import io.github.ikarenkov.sample.shikimori.impl.AnimesScreen
import io.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import com.github.terrakok.modo.Screen

class ShikimoriApi internal constructor(
    private val authFeature: AuthFeature
) {

    fun createScreen(): Screen = AnimesScreen()

    fun onIntentResult(intent: Intent) {
        authFeature.accept(AuthFeature.Msg.OAuthResult(intent.data.toString()))
    }
}
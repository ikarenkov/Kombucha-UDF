package com.github.ikarenkov.sample.shikimori.api

import android.content.Intent
import com.github.ikarenkov.sample.shikimori.impl.AnimesScreen
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthFeature
import com.github.ikarenkov.sample.shikimori.impl.auth.AuthStore
import com.github.terrakok.modo.Screen

class ShikimoriApi internal constructor(
    private val authFeature: AuthStore
) {

    fun createScreen(): Screen = AnimesScreen()

    fun onIntentResult(intent: Intent) {
        authFeature.dispatch(AuthFeature.Msg.OAuthResult(intent.data.toString()))
    }
}
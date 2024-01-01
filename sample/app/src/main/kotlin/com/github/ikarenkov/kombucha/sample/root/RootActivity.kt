package com.github.ikarenkov.kombucha.sample.root

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.github.ikarenkov.kombucha.sample.NavigationHolder
import logcat.logcat
import org.koin.java.KoinJavaComponent

class RootActivity : ComponentActivity() {

    private val navigationHolder = KoinJavaComponent.getKoin().get<NavigationHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logcat { "onCreate" }
        enableEdgeToEdge()
        window
        navigationHolder.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colors.background) {
                navigationHolder.rootScreen?.Content()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationHolder.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationHolder.onDestroy(isFinishing)
    }

}
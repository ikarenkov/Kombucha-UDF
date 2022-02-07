package ru.ikarenkov.teamaker.sample.root

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.compose.ComposeRender
import com.github.terrakok.modo.android.compose.init
import com.github.terrakok.modo.android.compose.saveState
import com.github.terrakok.modo.back
import logcat.logcat
import org.koin.android.ext.android.getKoin
import ru.ikarenkov.teamaker.sample.counter.api.counterFeatureFacade

class RootActivity : AppCompatActivity() {

    private val modo: Modo by lazy { getKoin().get() }
    private val render = ComposeRender(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logcat { "onCreate" }
        modo.init(savedInstanceState) { RootScreen() }
        setContent {
            render.Content()
        }
    }

    override fun onResume() {
        super.onResume()
        modo.render = render
    }

    override fun onPause() {
        super.onPause()
        modo.render = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        modo.saveState(outState)
    }

    override fun onBackPressed() {
        modo.back()
    }

}
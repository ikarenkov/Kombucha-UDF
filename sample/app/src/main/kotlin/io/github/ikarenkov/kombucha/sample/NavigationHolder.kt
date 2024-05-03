package io.github.ikarenkov.kombucha.sample

import android.os.Bundle
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.RootScreen
import com.github.terrakok.modo.stack.StackScreen
import io.github.ikarenkov.kombucha.sample.root.KombuchaRootScreen
import io.github.ikarenkov.kombucha.sample.root.SampleStack

class NavigationHolder {
    var rootScreen: RootScreen<StackScreen>? = null
        private set

    fun onCreate(savedInstanceState: Bundle?) {
        rootScreen = Modo.init(savedInstanceState, rootScreen) {
            SampleStack(KombuchaRootScreen())
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        Modo.save(outState, rootScreen)
    }

    fun onDestroy(isFinishing: Boolean) {
        if (isFinishing) {
            Modo.onRootScreenFinished(rootScreen)
        }
    }
}
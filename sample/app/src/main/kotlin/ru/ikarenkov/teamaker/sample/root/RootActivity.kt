package ru.ikarenkov.teamaker.sample.root

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import logcat.logcat
import org.koin.android.ext.android.getKoin
import ru.ikarenkov.teamaker.Store
import ru.ikarenkov.teamaker.instancekeeper.getStore

class RootActivity : AppCompatActivity() {

    private lateinit var store: Store<Msg, State, Eff>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logcat { "onCreate" }
        store = getStore { getKoin().createRootStore() }
        var state by mutableStateOf(store.currentState)
        store.listenState { state = it }
        setContent {
            RootScreen(state, store::accept)
        }
    }

}
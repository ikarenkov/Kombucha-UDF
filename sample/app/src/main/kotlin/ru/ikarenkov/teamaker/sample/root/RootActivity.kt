package ru.ikarenkov.teamaker.sample.root

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.ikarenkov.teamaker.Store
import ru.ikarenkov.teamaker.SyncStoreFactory
import ru.ikarenkov.teamaker.instancekeeper.getStore

class RootActivity : AppCompatActivity() {

    private lateinit var store: Store<Msg, State, Eff>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = getStore {
            SyncStoreFactory().create(
                "ROOT",
                State(0),
                rootReducer::invoke
            )
        }
        var state by mutableStateOf(store.currentState)
        store.listenState { state = it }
        setContent {
            RootScreen(state, store::accept)
        }
    }

}
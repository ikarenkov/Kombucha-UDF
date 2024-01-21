package ru.ikarenkov.kombucha.sample.counter.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.model.ScreenModel
import com.github.terrakok.modo.model.rememberScreenModel
import kotlinx.parcelize.Parcelize
import logcat.logcat
import org.koin.core.parameter.parametersOf
import ru.ikarenkov.kombucha.sample.counter.api.counterFeatureFacade
import ru.ikarenkov.kombucha.store.Store

@Parcelize
internal class CounterScreen(
    private var saveableState: State = State(0),
    override val screenKey: ScreenKey = generateScreenKey(),
) : Screen {

    @Composable
    override fun Content() {
        // TODO: lifecycle
        val tea = rememberScreenModel {
            counterFeatureFacade.scope.get<CounterScreenModel>(parameters = { parametersOf(State(0)) })
        }
        // Temp workaround to save state
        val state = tea.state.collectAsState()
        saveableState = state.value
        logcat { "CounterScreen Content, state is ${tea.state}" }
        CounterContent(counter = state.value.counter, dispatch = tea.store::dispatch)
    }

}

internal class CounterScreenModel(
    private var saveableState: State = State(0),
    counterStoreFactory: CounterCoroutineStoreFactory
) : ScreenModel {
    val store: Store<Msg, State, Eff> = counterStoreFactory.create(saveableState)
    val state = store.state

    override fun onDispose() {
        store.cancel()
    }

}

@Preview
@Composable
internal fun CounterContent(counter: Int = 0, dispatch: (Msg.Ui) -> Unit = {}) {
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .align(Alignment.Center)
                .width(IntrinsicSize.Max)
        ) {
            Text(counter.toString(), Modifier.align(Alignment.CenterHorizontally))
            FocusableButton(text = "Increase") {
                dispatch(Msg.Ui.OnIncreaseClick)
            }
            FocusableButton(text = "Decrease") {
                dispatch(Msg.Ui.OnDecreaseClick)
            }
            FocusableButton(text = "Open screen") {
                dispatch(Msg.Ui.OpenScreenClick)
            }
        }
    }
}

@Composable
private fun FocusableButton(
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
    ) {
        Text(text = text)
    }
}
package ru.ikarenkov.teamaker.sample.counter.impl

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import logcat.logcat
import ru.ikarenkov.teamaker.compose.ComposeTea
import ru.ikarenkov.teamaker.compose.asCompose
import ru.ikarenkov.teamaker.compose.brewComposeTea

@Parcelize
internal class CounterScreen(
    private var saveableState: State = State(0),
    override val screenKey: String = uniqueScreenKey,
) : ComposeScreen("counter") {

    // TODO: lifecycle
    @IgnoredOnParcel
    private val tea: ComposeTea<Msg.Ui, State, Eff> = createCounterStore(saveableState).asCompose()

    @Composable
    override fun Content() {
        val storeOwner = LocalViewModelStoreOwner.current!!
        // TODO: lifecycle
        val tea = remember(storeOwner) {
            storeOwner.brewComposeTea<Msg, Msg.Ui, State, Eff, Nothing>(screenKey) { createCounterStore(saveableState) }
        }
        // Temp workaround to save state
        saveableState = tea.state
        logcat { "CounterScreen Content, state is ${tea.state}" }
        CounterContent(counter = tea.state.counter, dispatch = tea::dispatch)
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
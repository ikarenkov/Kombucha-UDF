package ru.ikarenkov.teamaker.sample.counter.impl

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.parcelize.Parcelize
import logcat.logcat
import ru.ikarenkov.teamaker.compose.TeaScreen
import ru.ikarenkov.teamaker.compose.brewComposeTea

@Parcelize
internal class CounterScreen(
    override val screenKey: String = uniqueScreenKey
) :
    ComposeScreen("counter"),
    TeaScreen<Msg.Ui, State, Eff> by brewComposeTea(createCounterStore()) {

    init {
        logcat { "CounterScreen init, state is $state" }
        Log.d("CounterScreen", "init, state is $state")
    }

    @Composable
    override fun Content() {
        logcat { "CounterScreen Content, state is $state" }
        Log.d("CounterScreen", "Content, state is $state")
        CounterContent(counter = state.counter, dispatch = ::dispatch)
    }

}

@Preview
@Composable
internal fun CounterContent(counter: Int = 0, dispatch: (Msg.Ui) -> Unit = {}) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .align(Alignment.Center)
                .width(IntrinsicSize.Max)
        ) {
            Text(counter.toString(), Modifier.align(Alignment.CenterHorizontally))
            FocusableButton(text = "Increase", focusRequester) {
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
    requester: FocusRequester = FocusRequester(),
    onClick: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(requester)
            .onFocusChanged {
                isFocused = it.hasFocus
            }
            .focusable(),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isFocused) MaterialTheme.colors.primary else Color.Gray,
            contentColor = if (isFocused) MaterialTheme.colors.onPrimary else Color.White
        )
    ) {
        Text(text = text)
    }
}
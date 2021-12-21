package ru.ikarenkov.teamaker.sample.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RootScreen(state: State, dispatch: (Msg) -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            Text(state.counter.toString(), Modifier.align(Alignment.CenterHorizontally))
            Button(onClick = { dispatch(Msg.OnIncreaseClick) }) {
                Text(text = "Increase")
            }
            Button(onClick = { dispatch(Msg.OnDecreaseClick) }) {
                Text(text = "Decrease")
            }
        }
    }
}

@Preview
@Composable
private fun RootScreenPreview() {
    RootScreen(state = State(0)) {}
}
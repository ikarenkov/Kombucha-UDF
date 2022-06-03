package ru.ikarenkov.teamaker.learn_compose.impl

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
internal class LearnComposeScreen(
    override val screenKey: String = uniqueScreenKey
) : ComposeScreen("learn_compose") {

    @Composable
    override fun Content() {
        staticCompositionLocalOf {  }
        LocalContentColor
        val items = remember {
            List(10) { 0 }.flatMap { listOf(Color.Green, Color.Cyan, Color.Red) }
        }
        var topItem by remember { mutableStateOf(0) }
        CompositionLocalProvider(
            LocalIndication provides DefaultDebugIndication
        ) {
            CardsStack(items, topItem, onTopSwiped = { topItem++ }) {
                Box(
                    modifier = Modifier
                        .background(items[it].copy(alpha = 1f))
                        .size(300.dp)
                ) {
                    when (items[it]) {
                        Color.Green -> Text(text = "sfgdsfg dsg sdfg sdg sdg  ")
                        Color.Cyan -> Box {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Magenta)
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
internal fun LearnComposeContent(items: List<RowItem>) {
    LazyColumn {
        items(items.size) {
            Row(items[it])
        }
    }
}

data class RowItem(
    val title: String,
    val items: List<String>
)

@Composable
private fun Row(item: RowItem) {
    Column {
        Text(text = item.title)
        val scope = rememberCoroutineScope()
        val rowState = rememberLazyListState()
        LazyRow(state = rowState) {
            items(item.items.size) { index ->
                var hasFocus by remember { mutableStateOf(false) }
                Box(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .size(height = 90.dp, width = 160.dp)
                        .background(if (hasFocus) Color.Green else Color.Green)
                        .onFocusEvent {
                            hasFocus = it.hasFocus
                            if (hasFocus) {
                                scope.launch {
                                    rowState.animateScrollToItem(index, -100)
                                }
                            }
                        }
                        .clickable { }
                ) {
                    Text(text = item.items[index])
                }
            }
        }
    }
}

// Testing
object DefaultDebugIndication : Indication {

    private class DefaultDebugIndicationInstance(
        private val isPressed: State<Boolean>,
        private val isHovered: State<Boolean>,
        private val isFocused: State<Boolean>,
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (isPressed.value) {
                drawRect(color = Color.Black.copy(alpha = 0.3f), size = size)
            } else if (isHovered.value || isFocused.value) {
                drawRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                    size = size.copy(size.width - 4.dp.toPx(), size.height - 4.dp.toPx()),
                    style = Stroke(4.dp.toPx())
                )
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isPressed = interactionSource.collectIsPressedAsState()
        val isHovered = interactionSource.collectIsHoveredAsState()
        val isFocused = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource) {
            DefaultDebugIndicationInstance(isPressed, isHovered, isFocused)
        }
    }
}
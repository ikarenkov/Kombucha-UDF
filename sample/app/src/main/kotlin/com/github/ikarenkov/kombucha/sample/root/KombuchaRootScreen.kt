package com.github.ikarenkov.kombucha.sample.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.ikarenkov.kombucha.game.api.gameFeatureFacade
import com.github.ikarenkov.kombucha.learn_compose.api.learnComposeFeatureFacade
import com.github.ikarenkov.kombucha.sample.counter.api.counterFeatureFacade
import com.github.ikarenkov.sample.shikimori.api.shikimoriFeatureFacade
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize

@Parcelize
internal class KombuchaRootScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content() {
        RootScreen(LocalContainerScreen.current as StackScreen)
    }

}

@Composable
private fun RootScreen(parent: StackScreen) {
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .align(Alignment.Center)
                .width(IntrinsicSize.Max)
        ) {
            listOf(
                "Counter" to { counterFeatureFacade.api.createScreen() },
                "Learn compose" to { learnComposeFeatureFacade.api.screen() },
                "Game" to { gameFeatureFacade.api.createScreen() },
                "Shikimori" to { shikimoriFeatureFacade.api.createScreen() },
            ).forEach { (text, screen) ->
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { parent.forward(screen()) }
                ) {
                    Text(text = text)
                }
            }
        }
    }
}
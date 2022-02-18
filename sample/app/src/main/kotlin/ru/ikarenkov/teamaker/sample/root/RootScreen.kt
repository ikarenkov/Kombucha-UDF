package ru.ikarenkov.teamaker.sample.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import com.github.terrakok.modo.forward
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.koin.java.KoinJavaComponent.getKoin
import ru.ikarenkov.teamaker.game.api.gameFeatureFacade
import ru.ikarenkov.teamaker.learn_compose.api.learnComposeFeatureFacade
import ru.ikarenkov.teamaker.sample.counter.api.counterFeatureFacade

@Parcelize
internal class RootScreen(
    override val screenKey: String = uniqueScreenKey
) : ComposeScreen("Root Screen") {

    @IgnoredOnParcel
    private val modo: Modo by lazy { getKoin().get() }

    @Composable
    override fun Content() {
        RootScreen(modo)
    }

}

@Composable
fun RootScreen(modo: Modo) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            listOf(
                "Counter" to { counterFeatureFacade.api.createScreen() },
                "Learn compose" to { learnComposeFeatureFacade.api.screen() },
                "Game" to { gameFeatureFacade.api.createScreen() },
            ).forEach { (text, screen) ->
                Button(onClick = { modo.forward(screen()) }) {
                    Text(text = text)
                }
            }
        }
    }
}
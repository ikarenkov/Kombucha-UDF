package ru.ikarenkov.teamaker.game.impl.details.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.parcelize.Parcelize
import ru.ikarenkov.teamaker.game.impl.details.ui.theme.PodlodkaCrewComposeTaskTheme

@Parcelize
internal class GameDetailsScreen(override val screenKey: String = uniqueScreenKey) : ComposeScreen("Game details") {

    @Composable
    override fun Content() {
        ProvideWindowInsets {
//            val systemUiController = rememberSystemUiController()
//            SideEffect {
//                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)
//            }
            PodlodkaCrewComposeTaskTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    DetailsScreen()
                }
            }
        }
    }

}
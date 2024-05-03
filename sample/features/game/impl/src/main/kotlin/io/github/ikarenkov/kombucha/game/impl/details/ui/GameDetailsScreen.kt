package io.github.ikarenkov.kombucha.game.impl.details.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import io.github.ikarenkov.kombucha.game.impl.details.ui.theme.PodlodkaCrewComposeTaskTheme
import kotlinx.parcelize.Parcelize

@Parcelize
internal class GameDetailsScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content() {
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
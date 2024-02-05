package io.github.ikarenkov.sample.ui.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.Parcelize

@Parcelize
internal class DetailsScreen(
    val id: String,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {
    @Composable
    override fun Content() {
        Box(
            Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) {
            Text(text = "Id: $id", style = MaterialTheme.typography.h3)
        }
    }

}
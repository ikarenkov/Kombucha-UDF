package ru.ikarenkov.teamaker.learn_compose.impl

import androidx.compose.runtime.Composable
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
internal class FocusScreen(
    override val screenKey: String = uniqueScreenKey
) : ComposeScreen("focus_screen") {

    @Composable
    override fun Content() {
        FocusContent()
    }
}

@Composable
internal fun FocusContent() {
}
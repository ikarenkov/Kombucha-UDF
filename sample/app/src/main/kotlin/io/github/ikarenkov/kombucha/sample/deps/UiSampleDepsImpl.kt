package io.github.ikarenkov.kombucha.sample.deps

import com.github.terrakok.modo.stack.forward
import io.github.ikarenkov.kombucha.sample.NavigationHolder
import io.github.ikarenkov.sample.ui.api.UiSampleDeps
import io.github.ikarenkov.sample.ui.api.uiSampleFeatureFacade

class UiSampleDepsImpl(
    private val navigationHolder: NavigationHolder,
) : UiSampleDeps {
    override fun openDetailsScreen(id: String) {
        navigationHolder.rootScreen?.screen?.forward(uiSampleFeatureFacade.api.detailsScreen(id))
    }
}
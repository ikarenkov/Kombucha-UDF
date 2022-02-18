impl(
    packageName = "ru.ikarenkov.teamaker.game",
    compose = true,
    dependencies = deps(
        androidx.compose.base,
        androidx.compose.constraintLayout,
        androidx.compose.accompanist.insets,
        androidx.compose.accompanist.systemUiController,
        di.koinAndroid,
        log.logcat,
    ) + deps(
        tea.core,
        tea.compose,
        navigation.modoCompose,
        core.feature
    )
).withPlugin(Plugins.parcelize)
impl(
    packageName = "ru.ikarenkov.teamaker.sample.counter",
    compose = true,
    dependencies = deps(
        androidx.compose.base,
        di.koinAndroid,
        log.logcat,
    ) + deps(
        tea.core,
        tea.compose,
        navigation.modoCompose,
        core.feature
    )
).withPlugin(Plugins.parcelize)
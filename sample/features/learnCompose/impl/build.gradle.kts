impl(
    packageName = "ru.ikarenkov.teamaker.learn_compose",
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
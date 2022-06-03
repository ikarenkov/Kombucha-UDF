impl(
    packageName = "ru.ikarenkov.teamaker.sample.counter",
    compose = true,
    dependencies = deps(
        androidx.compose.base,
        androidx.compose.viewModel,
        di.koinAndroid,
        log.logcat,
        utils.instanceKeeper,
    ) + deps(
        tea.core,
        tea.compose,
        tea.instanceKeeperUtil,
        navigation.modoCompose,
        core.feature
    )
).withPlugin(Plugins.parcelize)
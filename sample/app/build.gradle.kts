androidApp(
    packageName = "ru.ikarenkov.teamaker.sample",
    dependencies = deps(
        androidx.appcompat,
        androidx.vectordrawable,
        google.material,
        androidx.compose.base,
        androidx.compose.activity,
        androidx.viewmodel,
        di.koinAndroid,
        log.logcat
    ) + deps(
        tea.core,
        tea.instanceKeeperUtil,
        navigation.modoCompose,
        core.feature
    ) + deps(
        target(":sample:features:counter:impl"),
        target(":sample:features:learnCompose:impl"),
    ),
    compose = true
).withPlugin(Plugins.parcelize)
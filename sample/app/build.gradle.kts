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
        tea.instanceKeeperUtil
    ),
    compose = true
)
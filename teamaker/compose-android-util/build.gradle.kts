androidUtil(
    packageName = "ru.ikarenkov.teamaker.compose",
    dependencies = deps(
        androidx.compose.runtime,
        androidx.viewmodel,
        utils.instanceKeeper
    ) + deps(
        tea.core,
        tea.instanceKeeperUtil,
    )
)
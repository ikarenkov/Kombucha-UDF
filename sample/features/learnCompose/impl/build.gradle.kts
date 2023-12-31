plugins {
    alias(libs.plugins.kombucha.android.library)
    alias(libs.plugins.kombucha.jetpackCompose.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.github.ikarenkov.sample.learn_compose"
}

dependencies {
    implementation(libs.koin.android)
    implementation(libs.debug.logcat)

    implementation(libs.modo)
    implementation(libs.androidx.compose.material)

    implementation(projects.teamaker.core)

    implementation(projects.sample.core.feature)

}
//impl(
//    packageName = "ru.ikarenkov.teamaker.learn_compose",
//    compose = true,
//    dependencies = deps(
//        androidx.compose.base,
//        di.koinAndroid,
//        log.logcat,
//    ) + deps(
//        tea.core,
//        tea.compose,
//        navigation.modoCompose,
//        core.feature
//    )
//).withPlugin(Plugins.parcelize)
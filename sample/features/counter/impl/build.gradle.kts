plugins {
    alias(libs.plugins.kombucha.android.library)
    alias(libs.plugins.kombucha.jetpackCompose.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "io.github.ikarenkov.sample.counter"
}

dependencies {
    implementation(libs.koin.android)
    implementation(libs.debug.logcat)

    implementation(libs.modo)
    implementation(libs.androidx.compose.material)

    implementation(projects.kombucha.core)

    implementation(projects.sample.core.feature)

}
plugins {
    alias(libs.plugins.kombucha.android.library)
    alias(libs.plugins.kombucha.jetpackCompose.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.github.ikarenkov.sample.game"
}

dependencies {
    implementation(libs.koin.android)
    implementation(libs.debug.logcat)

    implementation(libs.modo)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.constraintLayout)

    implementation(projects.kombucha.core)

    implementation(projects.sample.core.feature)

}
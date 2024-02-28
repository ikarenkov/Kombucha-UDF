plugins {
    alias(libs.plugins.kombucha.android.library)
    alias(libs.plugins.kombucha.jetpackCompose.library)
}

android.namespace = "io.github.ikarenkov.kombucha.sample.modo_kombucha"

dependencies {
    implementation(libs.koin.core)
    implementation(libs.modo)

    implementation(projects.kombucha.core)
    implementation(projects.kombucha.uiAdapter)
}
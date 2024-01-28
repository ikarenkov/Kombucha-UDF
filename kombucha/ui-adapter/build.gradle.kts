plugins {
    alias(libs.plugins.kombucha.android.library)
}

android.namespace = "io.github.ikarenkov.kombucha.ui_adapter"

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.kombucha.core)
}
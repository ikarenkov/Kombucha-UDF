plugins {
    alias(libs.plugins.kombucha.android.library)
    alias(libs.plugins.kombucha.jetpackCompose.library)
}

android {
    namespace = "io.github.ikarenkov.kobucha.instanceKeeper"
}

dependencies {
    implementation(projects.kombucha.core)

    implementation(libs.essenty.instanceKeeper)
    implementation(libs.androidx.lifecycle.viewmodel)
}
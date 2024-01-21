plugins {
    alias(libs.plugins.kombucha.android.library)
    alias(libs.plugins.kombucha.jetpackCompose.library)
}

android {
    namespace = "com.github.ikarenkov.kobucha.instanceKeeper"
}

dependencies {
    implementation(projects.teamaker.core)

    implementation(libs.essenty.instanceKeeper)
    implementation(libs.androidx.lifecycle.viewmodel)
}
plugins {
    alias(libs.plugins.kombucha.android.app)
    alias(libs.plugins.kombucha.jetpackCompose.app)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.github.ikarenkov.kombucha.sample"

    defaultConfig {
        applicationId = "com.github.ikarenkov.kombucha.sample"
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.material)
    implementation(libs.modo)
    implementation(libs.koin.android)

    implementation(libs.debug.logcat)

    implementation(projects.teamaker.core)
    implementation(projects.sample.core.feature)

    implementation(projects.sample.features.counter.impl)
    implementation(projects.sample.features.game.impl)
    implementation(projects.sample.features.learnCompose.impl)
}
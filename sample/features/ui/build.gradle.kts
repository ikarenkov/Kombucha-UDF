plugins {
    alias(libs.plugins.kombucha.android.library)
    alias(libs.plugins.kombucha.jetpackCompose.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "io.github.ikarenkov.sample.ui"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.koin.android)
    implementation(libs.debug.logcat)

    implementation(libs.modo)
    implementation(libs.androidx.compose.material)

    implementation(projects.kombucha.core)
    implementation(projects.kombucha.uiAdapter)

    implementation(projects.sample.core.feature)
    implementation(projects.sample.core.modoKombucha)

    testImplementation(libs.test.kotlin)
    testImplementation(libs.test.junit.jupiter)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.coroutines)
    testImplementation(projects.kombucha.test)
}
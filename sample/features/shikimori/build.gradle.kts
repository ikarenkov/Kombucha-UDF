plugins {
    alias(libs.plugins.kombucha.android.library)
    alias(libs.plugins.kombucha.jetpackCompose.library)
    alias(libs.plugins.kotlin.parcelize)
    kotlin("plugin.serialization") version libs.versions.kotlin
}

android {
    namespace = "io.github.ikarenkov.sample.shikomori"
}

tasks.withType<Test>() {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.koin.android)
    implementation(libs.debug.logcat)

    implementation(libs.modo)
    implementation(libs.androidx.compose.material)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.client.kotlinxSerializationJson)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.auth)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastorePreferences)

    implementation(projects.kombucha.core)
    implementation(projects.sample.core.feature)
    implementation(projects.sample.core.pagination)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    testImplementation(libs.test.kotlin)
    testImplementation(libs.test.junit.jupiter)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.coroutines)
    testImplementation(projects.kombucha.test)
}
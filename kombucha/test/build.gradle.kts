plugins {
    alias(libs.plugins.kombucha.jvm.library)
}

dependencies {
    implementation(projects.kombucha.core)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.test.kotlin)
    implementation(libs.test.coroutines)
}
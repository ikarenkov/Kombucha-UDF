plugins {
    alias(libs.plugins.kombucha.kmp.library)
    alias(libs.plugins.kombucha.publishing)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.kombucha.core)

            implementation(libs.test.kotlin)
            implementation(libs.test.coroutines)
        }
    }
}

plugins {
    alias(libs.plugins.kombucha.kmp.library)
}

kotlin {
    configureKmpLibrary("kombucha-udf-test")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.kombucha.core)

            implementation(libs.test.kotlin)
            implementation(libs.test.coroutines)
        }
    }
}

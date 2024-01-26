plugins {
    alias(libs.plugins.kombucha.kmp.library)
}

kotlin {
    configureKmpLibrary("kombucha-udf-core")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.test.kotlin)
        }
    }
}
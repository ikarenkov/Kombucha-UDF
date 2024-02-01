plugins {
    alias(libs.plugins.kombucha.kmp.library)
    alias(libs.plugins.kombucha.publishing)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.test.kotlin)
        }
    }
}
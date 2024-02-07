plugins {
    alias(libs.plugins.kombucha.kmp.library)
    alias(libs.plugins.kombucha.publishing)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.kombucha.core)
        }
        commonTest.dependencies {
            implementation(libs.test.kotlin)
            implementation(libs.test.coroutines)
        }
        jvmTest.dependencies {
            implementation(libs.test.junit.jupiter)
        }
    }
}
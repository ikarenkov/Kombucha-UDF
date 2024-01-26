import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.github.ikarenkov.kombucha.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    // workaround for https://github.com/gradle/gradle/issues/15383
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("kombucha-build-logic") {
            id = "kombucha-build-logic"
            implementationClass = "StubConventionPlugin"
        }
        register("kombucha-jvm-library") {
            id = "kombucha-jvm-library"
            implementationClass = "JvmLibraryPlugin"
        }
        register("kombucha-jetpack-compose-library") {
            id = "kombucha-jetpack-compose-library"
            implementationClass = "JetpackComposeLibraryPlugin"
        }
        register("kombucha-jetpack-compose-app") {
            id = "kombucha-jetpack-compose-app"
            implementationClass = "JetpackComposeAppPlugin"
        }
        register("kombucha-android-app") {
            id = "kombucha-android-app"
            implementationClass = "AndroidAppPlugin"
        }
        register("kombucha-android-library") {
            id = "kombucha-android-library"
            implementationClass = "AndroidLibraryPlugin"
        }
    }
}

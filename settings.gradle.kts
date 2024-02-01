pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "KombuchaUdf"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

apply(from = "kombucha/modules.gradle.kts")
apply(from = "sample/modules.gradle.kts")
include(":modo-compose")
include(":sample")
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "Kombucha"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

apply(from = "teamaker/modules.gradle.kts")
apply(from = "sample/modules.gradle.kts")
include(":modo-compose")
include(":sample")
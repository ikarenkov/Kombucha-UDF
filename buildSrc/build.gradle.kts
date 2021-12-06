plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation(project(":forma:android"))
    implementation(project(":forma:deps-core"))
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
}
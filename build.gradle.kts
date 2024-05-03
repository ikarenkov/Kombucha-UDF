plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kombucha.android.library) apply false
    alias(libs.plugins.kombucha.jetpackCompose.library) apply false
    alias(libs.plugins.kombucha.jetpackCompose.app) apply false
    alias(libs.plugins.kombucha.jvm.library) apply false
    alias(libs.plugins.kombucha.kmp.library) apply false
    alias(libs.plugins.kombucha.android.app) apply false
    alias(libs.plugins.kombucha.publishing) apply false

    alias(libs.plugins.kombucha.detekt)
}
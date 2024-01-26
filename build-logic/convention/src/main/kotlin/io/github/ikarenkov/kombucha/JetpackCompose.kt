package io.github.ikarenkov.kombucha

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.configureJetpackCompose(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    withVersionCatalog { libs ->

        commonExtension.apply {
            buildFeatures.compose = true
            composeOptions.kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtension.get()
        }

        dependencies {
            add("implementation", platform(libs.androidx.compose.bom))
            add("androidTestImplementation", platform(libs.androidx.compose.bom))

            add("implementation", libs.androidx.compose.foundation)
            add("implementation", libs.androidx.compose.animation)

            add("implementation", libs.androidx.compose.ui)

            add("debugImplementation", libs.androidx.compose.ui.tooling)
            add("implementation", libs.androidx.compose.ui.tooling.preview)
        }
    }
}
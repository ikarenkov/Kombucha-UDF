import io.github.ikarenkov.kombucha.configureKotlinJvm
import io.github.ikarenkov.kombucha.withVersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class KotlinMultiplatformLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            withVersionCatalog { libs ->
                pluginManager.apply(libs.plugins.kotlin.multiplatform.get().pluginId)
            }
            configureKotlinJvm()
        }
    }
}

fun KotlinMultiplatformExtension.jsLibrary(libraryName: String) {
    js {
        browser {
            webpackTask {
                mainOutputFileName = "$libraryName.js"
            }
        }
        binaries.executable()
    }
}

fun KotlinMultiplatformExtension.iosLibrary(libraryName: String) {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = libraryName
            isStatic = true
        }
    }
}

fun KotlinMultiplatformExtension.configureKmpLibrary(libraryName: String) {
    jvm()
    jsLibrary(libraryName)
    iosLibrary(libraryName)


    //https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<KotlinNativeTarget> {
        compilations["main"].compilerOptions.options.freeCompilerArgs.add("-Xexport-kdoc")
    }
}
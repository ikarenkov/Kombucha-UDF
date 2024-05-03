import com.android.build.api.dsl.LibraryExtension
import io.github.ikarenkov.kombucha.configureKotlinAndroid
import io.github.ikarenkov.kombucha.withVersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            withVersionCatalog {
                with(pluginManager) {
                    apply(libs.plugins.android.library.get().pluginId)
                    apply(libs.plugins.kotlin.android.get().pluginId)
                }
                configureKotlinAndroid(extensions.getByType<LibraryExtension>())
            }
        }
    }
}
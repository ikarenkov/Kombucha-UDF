import com.android.build.api.dsl.LibraryExtension
import com.github.ikarenkov.kombucha.configureKotlinAndroid
import com.github.ikarenkov.kombucha.withVersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            withVersionCatalog { libs ->
                with(pluginManager) {
                    apply(libs.plugins.android.library.get().pluginId)
                    apply(libs.plugins.kotlin.android.get().pluginId)
                }
                configureKotlinAndroid(extensions.getByType<LibraryExtension>())
            }
        }
    }
}
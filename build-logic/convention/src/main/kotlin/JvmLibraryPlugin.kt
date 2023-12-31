import com.github.ikarenkov.kombucha.configureKotlinJvm
import com.github.ikarenkov.kombucha.withVersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            withVersionCatalog { libs ->
                pluginManager.apply(libs.plugins.kotlin.jvm.get().pluginId)
                target.configureKotlinJvm()
            }
        }
    }
}
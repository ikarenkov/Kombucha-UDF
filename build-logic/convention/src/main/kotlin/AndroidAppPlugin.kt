import com.android.build.api.dsl.ApplicationExtension
import com.github.ikarenkov.kombucha.configureKotlinAndroid
import com.github.ikarenkov.kombucha.withVersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            withVersionCatalog { libs ->
                with(pluginManager) {
                    apply(libs.plugins.android.application.get().pluginId)
                    apply(libs.plugins.kotlin.android.get().pluginId)
                }
            }
            configureKotlinAndroid(extensions.getByType<ApplicationExtension>())
        }
    }
}
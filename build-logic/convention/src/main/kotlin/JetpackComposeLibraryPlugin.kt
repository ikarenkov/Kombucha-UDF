import com.android.build.gradle.LibraryExtension
import com.github.ikarenkov.kombucha.configureJetpackCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class JetpackComposeLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            configureJetpackCompose(extensions.getByType<LibraryExtension>())
        }
    }
}
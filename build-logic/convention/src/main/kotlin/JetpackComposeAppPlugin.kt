import com.android.build.api.dsl.ApplicationExtension
import io.github.ikarenkov.kombucha.configureJetpackCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class JetpackComposeAppPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            configureJetpackCompose(extensions.getByType<ApplicationExtension>())
        }
    }
}
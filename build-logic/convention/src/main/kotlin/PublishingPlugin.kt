import io.github.ikarenkov.kombucha.setupPublishing
import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("maven-publish")
                apply("signing")
            }
            setupPublishing()
        }
    }
}
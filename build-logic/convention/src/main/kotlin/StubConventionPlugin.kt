import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Just have this plugin to be able use util functions from the io.github.ikarenkov package
 */
class StubConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) = Unit
}
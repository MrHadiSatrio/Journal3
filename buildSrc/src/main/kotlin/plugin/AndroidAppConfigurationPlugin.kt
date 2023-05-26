package plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class AndroidAppConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configureAndroid()
        project.configureUnitTestForAndroid()
        project.configureJacocoForAndroid()
    }
}


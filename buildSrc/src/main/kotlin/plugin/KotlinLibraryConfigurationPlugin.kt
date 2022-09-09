package plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class KotlinLibraryConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configureKotlin()

        project.extensions.getByType<KotlinProjectExtension>().run {
            with(sourceSets) {
                getByName("main").kotlin.srcDir("src/main/kotlin")
                getByName("test").kotlin.srcDir("src/test/kotlin")
            }
        }
    }
}

package plugin

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class KotlinJvmLibraryConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configureKotlin()
        project.configureUnitTestForKotlinJvm()
        project.configureJacocoForJvm()

        project.extensions.getByType<KotlinJvmProjectExtension>().run {
            with(sourceSets) {
                getByName("main").kotlin.srcDir("src/main/kotlin")
                getByName("test").kotlin.srcDir("src/test/kotlin")
            }
        }

        project.extensions.getByType<JavaPluginExtension>().run {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }
}

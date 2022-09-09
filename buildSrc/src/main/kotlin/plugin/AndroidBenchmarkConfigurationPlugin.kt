package plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidBenchmarkConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configureAndroid()
        project.configureUnitTestForAndroid()
        project.configureJacocoForAndroid()

        project.extensions.getByType<BaseExtension>().run {
            defaultConfig {
                minSdk = 23
                testInstrumentationRunner("androidx.benchmark.junit4.AndroidBenchmarkRunner")
            }

            buildTypes.getByName("release") {
                isDefault = true
            }
        }

        project.dependencies {
            add("androidTestImplementation", Dependencies.AndroidPerformance.MICROBENCHMARK)
        }
    }
}


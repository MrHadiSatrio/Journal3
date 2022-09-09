package plugin

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

fun Project.configureKotlin() = this.extensions.getByType<KotlinJvmProjectExtension>().run {
    dependencies {
        add("implementation", Dependencies.Asynchrony.COROUTINES)
        add("testImplementation", Dependencies.TestUtility.COROUTINES_TEST)
        add("testImplementation", Dependencies.TestUtility.KOTEST_ASSERTIONS)
    }
}


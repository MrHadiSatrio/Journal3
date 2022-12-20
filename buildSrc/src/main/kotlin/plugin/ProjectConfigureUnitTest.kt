package plugin

import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import Dependencies
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureUnitTestForAndroid() = this.extensions.getByType<BaseExtension>().run {
    dependencies {
        add("testImplementation", Dependencies.TestRuntime.ANDROID_JUNIT_4)
        add("testImplementation", Dependencies.TestFramework.JUNIT_4)
        add("testImplementation", Dependencies.TestDouble.MOCKK)
        add("testImplementation", Dependencies.TestUtility.KOTEST_ASSERTIONS)
        add("testImplementation", Dependencies.TestUtility.ROBOLECTRIC)
    }
}

internal fun Project.configureUnitTestForKotlinJvm() = this.extensions.getByType<KotlinJvmProjectExtension>().run {
    dependencies {
        add("testRuntimeOnly", Dependencies.TestRuntime.JUNIT_5)
        add("testImplementation", Dependencies.TestFramework.JUNIT_5)
        add("testImplementation", Dependencies.TestDouble.MOCKK)
        add("testImplementation", Dependencies.TestUtility.KOTEST_ASSERTIONS)
    }
    project.tasks.withType(Test::class.java) { useJUnitPlatform() }
}

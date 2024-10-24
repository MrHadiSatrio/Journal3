import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import kotlinx.kover.gradle.plugin.dsl.MetricType

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.kover).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.sonar)
}

allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.xerial:sqlite-jdbc:3.46.0.1")
        }
    }
}

subprojects {
    val libs = rootProject.libs

    apply(plugin = libs.plugins.detekt.get().pluginId)
    extensions.configure<DetektExtension> {
        autoCorrect = true
        source.setFrom(
            "src/main/kotlin",
            "src/test/kotlin",
            "src/commonMain/kotlin",
            "src/commonTest/kotlin",
            "src/androidMain/kotlin",
            "src/androidUnitTest/kotlin",
            "src/androidTest/kotlin",
            "src/iosMain/kotlin",
            "src/iosTest/kotlin"
        )
    }
    dependencies {
        add("detektPlugins", libs.detekt.formatting)
    }

    apply(plugin = libs.plugins.kover.get().pluginId)
    extensions.configure<KoverReportExtension> {
        filters {
            excludes {
                classes("*Fake*", "*Test")
            }
        }
        defaults {
            verify {
                onCheck = true
                rule("Branch coverage must exceed 90%") {
                    isEnabled = true
                    entity = GroupingEntityType.APPLICATION

                    bound {
                        minValue = 90
                        metric = MetricType.BRANCH
                        aggregation = AggregationType.COVERED_PERCENTAGE
                    }
                }
            }
        }
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "MrHadiSatrio_Journal3")
        property("sonar.organization", "mrhadisatrio")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
    delete("$rootDir/buildSrc/build")
}

package plugin

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File

fun Project.configureJacocoForAndroid() {
    configureJacoco {
        tasks.register<JacocoReport>(this) {
            setDependsOn(setOf("testDebugUnitTest"))
            group = "verification"
            description = "Generate JaCoCo test report for Android."

            val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug")
            val mainSrc = "${project.projectDir}/src/main/kotlin"
            var excludedFiles = emptyList<String>()
            if (project.extra.has("fileFilter")) {
                excludedFiles = project.extra.get("fileFilter") as List<String>
            }

            sourceDirectories.setFrom(files(mainSrc))
            classDirectories.setFrom(debugTree.apply {
                setExcludes(excludedFiles)
            })

            executionData.setFrom(fileTree(buildDir).apply {
                setIncludes(setOf("jacoco/testDebugUnitTest.exec"))
            })
        }
    }

    tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        setDependsOn(setOf("jacocoTestReport"))
        group = "verification"
        description = "Runs JaCoCo test verification for Android."

        violationRules {
            rule {
                limit {
                    var minimumCoverage = "0.8"
                    if (project.extra.has("minimumCoverage")) {
                        minimumCoverage = project.extra.get("minimumCoverage").toString()
                    }
                    minimum = minimumCoverage.toBigDecimal()
                }
            }
        }

        val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug")
        val mainSrc = "${project.projectDir}/src/main/kotlin"
        var excludedFiles = emptyList<String>()
        if (project.extra.has("fileFilter")) {
            excludedFiles = project.extra.get("fileFilter") as List<String>
        }

        sourceDirectories.setFrom(files(mainSrc))
        classDirectories.setFrom(debugTree.apply {
            setExcludes(excludedFiles)
        })

        executionData.setFrom(fileTree(buildDir).apply {
            setIncludes(setOf("jacoco/testDebugUnitTest.exec"))
        })
    }
}

fun Project.configureJacocoForJvm() {
    configureJacoco {
        tasks.named<JacocoReport>(this).configure {
            dependsOn(tasks.named("test"))
        }
    }

    tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification").configure {
        dependsOn(tasks.named("jacocoTestReport"))
        group = "verification"
        description = "Runs JaCoCo test verification for JVM."

        violationRules {
            rule {
                limit {
                    var minimumCoverage = "0.8"
                    if (project.extra.has("minimumCoverage")) {
                        minimumCoverage = project.extra.get("minimumCoverage").toString()
                    }
                    minimum = minimumCoverage.toBigDecimal()
                }
            }
        }

        val debugTreeJava = fileTree("${project.buildDir}/classes/java")
        val debugTreeKotlin = fileTree("${project.buildDir}/classes/kotlin")
        val mainSrc = "${project.projectDir}/src/main/kotlin"

        sourceDirectories.setFrom(files(mainSrc))
        classDirectories.setFrom(files(debugTreeJava, debugTreeKotlin))

        executionData.setFrom(fileTree(buildDir).apply {
            setIncludes(setOf("jacoco/test.exec"))
        })
    }
}

private fun Project.configureJacoco(configuration: String.() -> Unit) {
    apply<JacocoPlugin>()

    extensions.getByType(JacocoPluginExtension::class.java).toolVersion = "0.8.8"

    val task = "jacocoTestReport"

    task.configuration()

    tasks.named<JacocoReport>(task).configure {
        reports.apply {
            xml.apply {
                required.set(true)
                outputLocation.set(File("${project.buildDir}/reports/jacocoTestReport.xml"))
            }
            html.apply {
                required.set(true)
                outputLocation.set(File("${project.buildDir}/reports/jacoco"))
            }
        }
    }
}


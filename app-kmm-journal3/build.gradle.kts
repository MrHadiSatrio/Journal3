plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kover)
}

kotlin {
    jvm()
    androidTarget()

    jvmToolchain(17)

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":lib-kmm-foundation"))
                api(project(":lib-kmm-io"))
                api(project(":lib-kmm-json"))
                api(project(":lib-kmm-geography"))
                api(project(":lib-kmm-paraphrase"))
                api(libs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.reactive.extensions.test)
                implementation(libs.kotest.assertions)
                implementation(libs.okio.fake.fs)
                implementation(libs.mockk)
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
    }
}

android {
    namespace = "com.hadisatrio.apps.kotlin.journal3"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 33
    }
}

koverReport {
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
                entity = kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.APPLICATION

                bound {
                    minValue = 90
                    metric = kotlinx.kover.gradle.plugin.dsl.MetricType.BRANCH
                    aggregation = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
                }
            }
        }
    }
}

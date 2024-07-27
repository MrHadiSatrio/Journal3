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
                api(project(":lib-kmm-json"))
                api(libs.uuid)
                api(libs.kotlinx.datetime)
                api(libs.ktor)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.kotest.assertions)
                implementation(libs.robolectric)
                implementation(libs.mockk)
                implementation(libs.ktor.mock.engine)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.assent)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.androidx.test.runner)
                implementation(libs.junit4)
                implementation(libs.kotest.assertions)
            }
        }
    }
}

android {
    namespace = "com.hadisatrio.libs.android.geography"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 33
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
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

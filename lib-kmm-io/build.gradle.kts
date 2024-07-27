plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt)
}

kotlin {
    jvm()
    androidTarget()

    jvmToolchain(17)

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.okio)
                api(libs.uri)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.kotest.assertions)
                implementation(libs.okio.fake.fs)
                implementation(libs.mockk)
            }
        }
        val androidMain by getting
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.androidx.test.runner)
                implementation(libs.junit4)
                implementation(libs.kotest.assertions)
                implementation(libs.robolectric)
                implementation(libs.mockk)
            }
        }
    }
}

android {
    namespace = "com.hadisatrio.libs.android.io"
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

detekt {
    autoCorrect = true
    source = files(
        "src/commonMain/kotlin",
        "src/commonTest/kotlin",
        "src/androidMain/kotlin",
        "src/androidUnitTest/kotlin",
        "src/iosMain/kotlin"
    )
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
}

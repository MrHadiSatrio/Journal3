apply("$rootDir/gradle/script-ext.gradle")

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover")
    id("io.gitlab.arturbosch.detekt")
}

kotlin {
    jvm()
    androidTarget()

    jvmToolchain(17)

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Dependencies.Commons.UUID)
                api(Dependencies.Asynchrony.REACTIVE_EXTENSIONS)
                implementation(Dependencies.Commons.DATETIME)
                implementation(Dependencies.Asynchrony.REACTIVE_EXTENSIONS_COROUTINE_INTEROP)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(Dependencies.TestUtility.REACTIVE_EXTENSIONS_TEST)
                implementation(Dependencies.TestUtility.KOTEST_ASSERTIONS)
                implementation(Dependencies.TestDouble.MOCKK)
            }
        }
        val androidMain by getting {
            dependencies {
                api(Dependencies.AndroidCompatibility.APPCOMPAT)
                api(Dependencies.AndroidUi.RECYCLER_VIEW)
                api(Dependencies.AndroidUi.FLOW_BINDING)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(Dependencies.TestRuntime.ANDROID_JUNIT_4)
                implementation(Dependencies.TestFramework.JUNIT_4)
                implementation(Dependencies.TestUtility.KOTEST_ASSERTIONS)
                implementation(Dependencies.TestUtility.ROBOLECTRIC)
                implementation(Dependencies.TestDouble.MOCKK)
            }
        }
    }
}

android {
    namespace = "com.hadisatrio.libs.android.foundation"
    compileSdk = Dependencies.AndroidSdk.COMPILE
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Dependencies.AndroidSdk.MINIMUM
        targetSdk = Dependencies.AndroidSdk.TARGET
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
        "src/androidUnitTest/kotlin"
    )
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
}

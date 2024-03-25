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
                api(project(":lib-kmm-foundation"))
                api(project(":lib-kmm-io"))
                api(project(":lib-kmm-json"))
                api(project(":lib-kmm-geography"))
                api(project(":lib-kmm-paraphrase"))
                api(Dependencies.Commons.DATETIME)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(Dependencies.TestUtility.REACTIVE_EXTENSIONS_TEST)
                implementation(Dependencies.TestUtility.KOTEST_ASSERTIONS)
                implementation(Dependencies.TestDouble.OKIO_FAKE_FS)
                implementation(Dependencies.TestDouble.MOCKK)
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
    }
}

android {
    namespace = "com.hadisatrio.apps.kotlin.journal3"
    compileSdk = Dependencies.AndroidSdk.COMPILE
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Dependencies.AndroidSdk.MINIMUM
        targetSdk = Dependencies.AndroidSdk.TARGET
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

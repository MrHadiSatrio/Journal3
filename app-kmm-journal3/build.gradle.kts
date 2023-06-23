apply("$rootDir/gradle/script-ext.gradle")

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover")
    id("io.gitlab.arturbosch.detekt")
}

kotlin {
    jvm()
    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":lib-kmm-foundation"))
                api(project(":lib-kmm-io"))
                api(project(":lib-kmm-json"))
                api(project(":lib-kmm-geography"))
                api(Dependencies.Commons.DATETIME)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(Dependencies.TestUtility.COROUTINES_TEST)
                implementation(Dependencies.TestUtility.KOTEST_ASSERTIONS)
                implementation(Dependencies.TestDouble.OKIO_FAKE_FS)
                implementation(Dependencies.TestDouble.MOCKK)
            }
        }
        val androidMain by getting
        val androidTest by getting
    }
}

android {
    compileSdk = Dependencies.AndroidSdk.COMPILE
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Dependencies.AndroidSdk.MINIMUM
        targetSdk = Dependencies.AndroidSdk.TARGET
    }
}

kover {
    filters {
        classes {
            excludes += listOf("*Fake*", "*Test")
        }
    }
    verify {
        onCheck.set(true)
        rule {
            isEnabled = true
            name = "Branch coverage must exceed 90%"
            target = kotlinx.kover.api.VerificationTarget.ALL

            bound {
                minValue = 90
                counter = kotlinx.kover.api.CounterType.BRANCH
                valueType = kotlinx.kover.api.VerificationValueType.COVERED_PERCENTAGE
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
        "src/androidTest/kotlin"
    )
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
}

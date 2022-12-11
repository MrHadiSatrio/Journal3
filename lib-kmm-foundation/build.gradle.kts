apply("$rootDir/gradle/script-ext.gradle")

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover")
    id("io.gitlab.arturbosch.detekt")
}

version = ext.get("gitVersionName")!!

kotlin {
    jvm()
    android()
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "lib-kotlin-foundation"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Dependencies.Commons.UUID)
                api(Dependencies.Asynchrony.COROUTINES)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(Dependencies.TestUtility.COROUTINES_TEST)
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
        val androidTest by getting {
            dependencies {
                implementation(Dependencies.TestRuntime.ANDROID_JUNIT_4)
                implementation(Dependencies.TestFramework.JUNIT_4)
                implementation(Dependencies.TestUtility.KOTEST_ASSERTIONS)
                implementation(Dependencies.TestUtility.ROBOLECTRIC)
                implementation(Dependencies.TestDouble.MOCKK)
            }
        }
        // val iosX64Main by getting
        // val iosArm64Main by getting
        // val iosSimulatorArm64Main by getting
        // val iosMain by creating {
        //     dependsOn(commonMain)
        //     iosX64Main.dependsOn(this)
        //     iosArm64Main.dependsOn(this)
        //     iosSimulatorArm64Main.dependsOn(this)
        // }
        // val iosX64Test by getting
        // val iosArm64Test by getting
        // val iosSimulatorArm64Test by getting
        // val iosTest by creating {
        //     dependsOn(commonTest)
        //     iosX64Test.dependsOn(this)
        //     iosArm64Test.dependsOn(this)
        //     iosSimulatorArm64Test.dependsOn(this)
        // }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
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
        "src/androidTest/kotlin",
        "src/iosMain/kotlin"
    )
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0")
}

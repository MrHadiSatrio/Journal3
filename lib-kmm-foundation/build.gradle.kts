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
                api(libs.uuid)
                api(libs.reactive.extensions)
                implementation(libs.kotlinx.datetime)
                implementation(libs.reactive.extensions.coroutine)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.reactive.extensions.test)
                implementation(libs.kotest.assertions)
                implementation(libs.mockk)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.androidx.appcompat)
                api(libs.androidx.recyclerview)
                api(libs.flow.binding)
            }
        }
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
    namespace = "com.hadisatrio.libs.android.foundation"
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

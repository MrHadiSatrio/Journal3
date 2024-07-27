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
                api(project(":lib-kmm-json"))
                api(libs.ktor)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.kotest.assertions)
                implementation(libs.mockk)
                implementation(libs.ktor.mock.engine)
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
    }
}

android {
    namespace = "com.hadisatrio.libs.android.paraphrase"
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

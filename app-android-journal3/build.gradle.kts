import plugin.AndroidAppConfigurationPlugin

apply<AndroidAppConfigurationPlugin>()
apply("$rootDir/gradle/script-ext.gradle")

val version = ext.get("gitVersionName")

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("io.gitlab.arturbosch.detekt")
}

android {
    buildTypes {
        debug {
            buildConfigField("String", "KEY_HERE_API", "\"${System.getenv("DEBUG_KEY_HERE_API")}\"")
        }
        release {
            buildConfigField("String", "KEY_HERE_API", "\"${System.getenv("DEBUG_KEY_HERE_API")}\"")
        }
    }
}

dependencies {
    implementation(project(":app-kmm-journal3"))
    implementation(Dependencies.AndroidArchitecture.STARTUP)
    implementation(Dependencies.AndroidAsynchrony.WORKMANAGER)
    implementation(Dependencies.AndroidNetwork.KTOR)
}

detekt {
    autoCorrect = true
    source = files("src/main/kotlin", "src/test/kotlin", "src/androidTest/kotlin")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
}

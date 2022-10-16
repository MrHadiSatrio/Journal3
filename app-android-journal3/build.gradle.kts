import plugin.AndroidAppConfigurationPlugin

apply<AndroidAppConfigurationPlugin>()
apply("$rootDir/gradle/script-ext.gradle")

val version = ext.get("gitVersionName")

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("io.gitlab.arturbosch.detekt")
}

dependencies {
    implementation(project(":app-kmm-journal3"))
    implementation(Dependencies.AndroidArchitecture.STARTUP)
    implementation(Dependencies.AndroidAsynchrony.WORKMANAGER)
}

detekt {
    autoCorrect = true
    source = files("src/main/kotlin", "src/test/kotlin", "src/androidTest/kotlin")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0")
}

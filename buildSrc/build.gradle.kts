buildscript {
    repositories {
        mavenCentral()
        google()
    }
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()

    maven {
        name = "Gradle libs"
        url = uri("https://repo.gradle.org/gradle/libs")
    }
    maven {
        name = "Gradle Snapshot libs"
        url = uri("https://repo.gradle.org/gradle/libs-snapshots")
    }
    maven {
        name = "KotlinX"
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
}

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:8.1.4")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
}

plugins {
    id("scripts.infrastructure")
    id("org.jetbrains.kotlinx.kover").version("0.7.4")
    id("io.gitlab.arturbosch.detekt").version("1.23.4")
    id("org.sonarqube").version("4.0.0.2929")
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        maven(url = "https://ajoberstar.org/bintray-backup/")
    }
}

allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://repo.repsy.io/mvn/chrynan/public")
    }
    configurations.all {
        resolutionStrategy {
            force("org.xerial:sqlite-jdbc:3.44.0.0")
        }
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "MrHadiSatrio_Journal3")
        property("sonar.organization", "mrhadisatrio")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
    delete("$rootDir/buildSrc/build")
}

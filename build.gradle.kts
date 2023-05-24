plugins {
    id("scripts.infrastructure")
    id("org.ajoberstar.grgit").version("4.1.1")
    id("org.jetbrains.dokka").version("1.7.0")
    id("org.jetbrains.kotlinx.kover").version("0.7.0")
    id("io.gitlab.arturbosch.detekt").version("1.21.0")
    id("org.barfuin.gradle.jacocolog").version("2.0.0")
    id("org.sonarqube").version("3.5.0.2730")
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        maven(url = "https://ajoberstar.org/bintray-backup/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("androidx.benchmark:benchmark-gradle-plugin:1.1.1")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.7.0")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.28.4")
        classpath("org.jetbrains.kotlinx:kover:0.6.1")
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.0")
    }
}

allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }
    configurations.all {
        resolutionStrategy {
            force("org.xerial:sqlite-jdbc:3.34.0")
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

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(file("${rootProject.projectDir}/public"))
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
    delete("$rootDir/buildSrc/build")
}

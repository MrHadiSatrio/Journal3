plugins {
    id("scripts.infrastructure")
    id("org.ajoberstar.grgit").version("5.2.1")
    id("org.jetbrains.dokka").version("1.9.10")
    id("org.jetbrains.kotlinx.kover").version("0.7.4")
    id("io.gitlab.arturbosch.detekt").version("1.23.2")
    id("org.barfuin.gradle.jacocolog").version("3.1.0")
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
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath("androidx.benchmark:benchmark-gradle-plugin:1.2.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.10")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:5.1.10")
        classpath("org.jetbrains.kotlinx:kover:0.6.1")
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.3")
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
            force("org.xerial:sqlite-jdbc:3.43.2.2")
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

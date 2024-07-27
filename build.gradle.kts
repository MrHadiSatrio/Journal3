plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.kover).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.sonar)
}

allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.xerial:sqlite-jdbc:3.46.0.0")
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

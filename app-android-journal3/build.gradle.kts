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
    signingConfigs {
        getByName("debug") {
            storeFile = File(projectDir, "debug_signing.jks")
            storePassword = "(debug)"
            keyAlias = "debug"
            keyPassword = "(debug)"
        }
        create("release") {
            storeFile = File(projectDir, "release_signing.jks")
            storePassword = System.getenv("RELEASE_KEY_STORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "KEY_HERE_API", "\"${System.getenv("DEBUG_KEY_HERE_API")}\"")
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("String", "KEY_HERE_API", "\"${System.getenv("RELEASE_KEY_HERE_API")}\"")
        }
    }

    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    implementation(project(":app-kmm-journal3"))
    implementation(Dependencies.AndroidArchitecture.STARTUP)
    implementation(Dependencies.AndroidAsynchrony.WORKMANAGER)
    implementation(Dependencies.AndroidFramework.GLIDE)
    implementation(Dependencies.AndroidNetwork.KTOR)
    implementation(Dependencies.AndroidUi.RECYCLER_VIEW_SPACING_DECORATION)
}

detekt {
    autoCorrect = true
    source = files("src/main/kotlin", "src/test/kotlin", "src/androidTest/kotlin")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
}

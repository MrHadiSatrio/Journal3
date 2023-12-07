import plugin.AndroidAppConfigurationPlugin

apply<AndroidAppConfigurationPlugin>()
apply("$rootDir/gradle/script-ext.gradle")

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("io.sentry.android.gradle") version "3.14.0"
    id("io.gitlab.arturbosch.detekt")
    id("io.github.reactivecircus.app-versioning").version("1.3.1")
}

android {
    namespace = "com.hadisatrio.apps.android.journal3"

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
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = "canary"
            buildConfigField("String", "KEY_HERE_API", "\"${System.getenv("DEBUG_KEY_HERE_API")}\"")
            buildConfigField("String", "KEY_OAI_API", "\"${System.getenv("DEBUG_KEY_OAI_API")}\"")
            buildConfigField("String", "KEY_SENTRY", "\"${System.getenv("DEBUG_KEY_SENTRY")}\"")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "KEY_HERE_API", "\"${System.getenv("RELEASE_KEY_HERE_API")}\"")
            buildConfigField("String", "KEY_OAI_API", "\"${System.getenv("RELEASE_KEY_OAI_API")}\"")
            buildConfigField("String", "KEY_SENTRY", "\"${System.getenv("RELEASE_KEY_SENTRY")}\"")
        }
    }

    packaging {
        resources.excludes += "**/*"
    }
}

dependencies {
    implementation(project(":app-kmm-journal3"))
    implementation(Dependencies.AndroidArchitecture.FRAGMENT)
    implementation(Dependencies.AndroidArchitecture.STARTUP)
    implementation(Dependencies.AndroidAsynchrony.WORKMANAGER)
    implementation(Dependencies.AndroidFramework.GLIDE)
    implementation(Dependencies.AndroidFramework.TFLITE)
    implementation(Dependencies.AndroidFramework.SENTRY)
    implementation(Dependencies.AndroidNetwork.KTOR)
    implementation(Dependencies.AndroidUi.VIEWPAGER)
    implementation(Dependencies.AndroidUi.RECYCLER_VIEW_SPACING_DECORATION)
}

detekt {
    autoCorrect = true
    source = files("src/main/kotlin", "src/test/kotlin", "src/androidTest/kotlin")
}

sentry {
    org.set("mrhadisatrio")
    projectName.set("journal3")
    authToken.set(System.getenv("SENTRY_TOKEN"))
    includeSourceContext.set(true)
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.4")
}

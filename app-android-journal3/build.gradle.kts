plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.sentry)
    alias(libs.plugins.appVersioning)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.hadisatrio.apps.android.journal3"

    compileSdk = 34
    defaultConfig {
        applicationId = "com.hadisatrio.apps.android.journal3"
        minSdk = 23
        targetSdk = 33
    }

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
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.workmanager)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.glide)
    implementation(libs.ktor)
    implementation(libs.ktor.android)
    implementation(libs.material)
    implementation(libs.sentry)
    implementation(libs.tflite)
    implementation(libs.recycler.view.spacing)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.junit4)
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.robolectric)
}

sentry {
    org.set("mrhadisatrio")
    projectName.set("journal3")
    authToken.set(System.getenv("SENTRY_TOKEN"))
    includeSourceContext.set(true)
}

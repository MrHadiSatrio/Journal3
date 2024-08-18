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
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig {
        applicationId = "com.hadisatrio.apps.android.journal3"
        minSdk = 23
        targetSdk = 33
        buildConfigField(
            type = "String",
            name = "OPENTELEMETRY_AUTHORITY",
            value = project.findProperty("opentelemetry.authority") as String? ?: "\"10.0.2.2:4318\""
        )
        buildConfigField(
            type = "String",
            name = "OPENTELEMETRY_TRACES_PATH",
            value = project.findProperty("opentelemetry.traces.path") as String? ?: "\"v1/traces\""
        )
        buildConfigField(
            type = "String",
            name = "OPENTELEMETRY_LOGS_PATH",
            value = project.findProperty("opentelemetry.logs.path") as String? ?: "\"v1/logs\""
        )
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
    coreLibraryDesugaring(libs.desugar)
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
    implementation(libs.opentelemetry.android)
    implementation(libs.opentelemetry.exporter.otlp)
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

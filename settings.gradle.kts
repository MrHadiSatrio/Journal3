enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.hadisatrio.libs.android.ruler") {
                useModule("com.hadisatrio.libs.android:ruler-gradle-plugin:1.0.0-alpha.2")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://repo.repsy.io/mvn/chrynan/public")
    }
}

rootProject.name = "Journal3"
include(":app-android-journal3")
include(":app-kmm-journal3")
include(":lib-kmm-geography")
include(":lib-kmm-paraphrase")
include(":lib-kmm-frontmatter")
include(":lib-kmm-json")
include(":lib-kmm-io")
include(":lib-kmm-collection")
include(":lib-kmm-foundation")

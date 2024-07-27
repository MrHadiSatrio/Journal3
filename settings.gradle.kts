enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
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
include(":lib-kmm-json")
include(":lib-kmm-io")
include(":lib-kmm-foundation")

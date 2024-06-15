@file:Suppress("unused", "ClassName")

object Dependencies {

    object CollectiveDependencyVersions {
        const val ANDROIDX_LIFECYCLE = "2.8.2"
        const val JUNIT_5 = "5.10.1"
        const val KTOR = "2.3.7"
        const val OKIO = "3.7.0"
        const val REACTIVE_EXTENSIONS = "2.0.1"
    }

    object AndroidSdk {
        const val MINIMUM = 23
        const val COMPILE = 34
        const val TARGET = 32
    }

    object AndroidArchitecture {
        const val FRAGMENT = "androidx.fragment:fragment-ktx:1.6.2"
        const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${CollectiveDependencyVersions.ANDROIDX_LIFECYCLE}"
        const val LIFECYCLE = "androidx.lifecycle:lifecycle-runtime-ktx:${CollectiveDependencyVersions.ANDROIDX_LIFECYCLE}"
        const val STARTUP = "androidx.startup:startup-runtime:1.1.1"
    }

    object AndroidAsynchrony {
        const val WORKMANAGER = "androidx.work:work-runtime-ktx:2.9.0"
    }

    object AndroidCompatibility {
        const val CORE_KTX = "androidx.core:core-ktx:1.12.0"
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.7.0"
    }

    object AndroidFramework {
        const val GLIDE = "com.github.bumptech.glide:glide:4.16.0"
        const val TFLITE = "org.tensorflow:tensorflow-lite-task-text:0.4.4"
        const val SENTRY = "io.sentry:sentry-android:7.10.0"
    }

    object AndroidNetwork {
        const val KTOR = "io.ktor:ktor-client-android:${CollectiveDependencyVersions.KTOR}"
    }

    object AndroidUi {
        const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.3.2"
        const val VIEWPAGER = "androidx.viewpager2:viewpager2:1.0.0"
        const val MATERIAL = "com.google.android.material:material:1.11.0"
        const val FLOW_BINDING = "io.github.reactivecircus.flowbinding:flowbinding-android:1.2.0"
        const val RECYCLER_VIEW_SPACING_DECORATION = "com.github.grzegorzojdana:SpacingItemDecoration:1.1.0"
    }

    object AndroidPerformance {
        const val MICROBENCHMARK = "androidx.benchmark:benchmark-junit4:1.2.4"
    }

    object AndroidSecurity {
        const val ASSENT = "com.afollestad.assent:core:3.0.2"
    }

    object Asynchrony {
        const val REACTIVE_EXTENSIONS = "com.badoo.reaktive:reaktive:${CollectiveDependencyVersions.REACTIVE_EXTENSIONS}"
        const val REACTIVE_EXTENSIONS_COROUTINE_INTEROP = "com.badoo.reaktive:coroutines-interop:${CollectiveDependencyVersions.REACTIVE_EXTENSIONS}"
    }

    object Commons {
        const val UUID = "com.benasher44:uuid:0.8.2"
        const val URI = "com.chrynan.uri:uri-core:0.4.0"
        const val DATETIME = "org.jetbrains.kotlinx:kotlinx-datetime:0.5.0"
        const val KOTLINX_JSON_OKIO = "org.jetbrains.kotlinx:kotlinx-serialization-json-okio:1.6.2"
        const val OKIO = "com.squareup.okio:okio:${CollectiveDependencyVersions.OKIO}"
    }

    object Network {
        const val KTOR = "io.ktor:ktor-client-core:${CollectiveDependencyVersions.KTOR}"
    }

    object TestRuntime {
        const val ANDROID_JUNIT_4 = "androidx.test:runner:1.5.2"
        const val JUNIT_5 = "org.junit.jupiter:junit-jupiter-engine:${CollectiveDependencyVersions.JUNIT_5}"
    }

    object TestFramework {
        const val JUNIT_4 = "junit:junit:4.13.2"
        const val JUNIT_5 = "org.junit.jupiter:junit-jupiter-api:${CollectiveDependencyVersions.JUNIT_5}"
        const val ANDROID_JUNIT = "androidx.test.ext:junit:1.1.5"
    }

    object TestUtility {
        const val REACTIVE_EXTENSIONS_TEST = "com.badoo.reaktive:reaktive-testing:${CollectiveDependencyVersions.REACTIVE_EXTENSIONS}"
        const val KOTEST_ASSERTIONS = "io.kotest:kotest-assertions-core:5.8.0"
        const val ESPRESSO = "androidx.test.espresso:espresso-core:3.5.1"
        const val ROBOLECTRIC = "org.robolectric:robolectric:4.11.1"
    }

    object TestDouble {
        const val MOCKK = "io.mockk:mockk:1.13.11"
        const val OKIO_FAKE_FS = "com.squareup.okio:okio-fakefilesystem:${CollectiveDependencyVersions.OKIO}"
        const val KTOR_MOCK_ENGINE = "io.ktor:ktor-client-mock:${CollectiveDependencyVersions.KTOR}"
    }
}


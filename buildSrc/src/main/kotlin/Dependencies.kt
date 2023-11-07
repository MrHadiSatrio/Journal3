@file:Suppress("unused", "ClassName")

object Dependencies {

    object AndroidSdk {
        const val MINIMUM = 23
        const val COMPILE = 34
        const val TARGET = 32
    }

    object AndroidArchitecture {
        const val FRAGMENT = "androidx.fragment:fragment-ktx:1.6.1"
        const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2"
        const val LIFECYCLE = "androidx.lifecycle:lifecycle-runtime-ktx:2.6.2"
        const val STARTUP = "androidx.startup:startup-runtime:1.1.1"
    }

    object AndroidAsynchrony {
        const val WORKMANAGER = "androidx.work:work-runtime-ktx:2.8.1"
    }

    object AndroidCompatibility {
        const val CORE_KTX = "androidx.core:core-ktx:1.12.0"
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.6.1"
    }

    object AndroidFramework {
        const val GLIDE = "com.github.bumptech.glide:glide:4.16.0"
        const val TFLITE = "org.tensorflow:tensorflow-lite-task-text:0.4.4"
        const val SENTRY = "io.sentry:sentry-android:6.32.0"
    }

    object AndroidNetwork {
        const val KTOR = "io.ktor:ktor-client-android:2.3.6"
    }

    object AndroidUi {
        const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.3.2"
        const val VIEWPAGER = "androidx.viewpager2:viewpager2:1.0.0"
        const val MATERIAL = "com.google.android.material:material:1.10.0"
        const val FLOW_BINDING = "io.github.reactivecircus.flowbinding:flowbinding-android:1.2.0"
        const val RECYCLER_VIEW_SPACING_DECORATION = "com.github.grzegorzojdana:SpacingItemDecoration:1.1.0"
    }

    object AndroidPerformance {
        const val MICROBENCHMARK = "androidx.benchmark:benchmark-junit4:1.2.0"
    }

    object AndroidSecurity {
        const val ASSENT = "com.afollestad.assent:core:3.0.2"
    }

    object Asynchrony {
        const val REACTIVE_EXTENSIONS = "com.badoo.reaktive:reaktive:1.3.0"
        const val REACTIVE_EXTENSIONS_COROUTINE_INTEROP = "com.badoo.reaktive:coroutines-interop:1.3.0"
    }

    object Commons {
        const val UUID = "com.benasher44:uuid:0.8.1"
        const val URI = "com.chrynan.uri:uri-core:0.4.0"
        const val DATETIME = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.1"
        const val KOTLINX_JSON_OKIO = "org.jetbrains.kotlinx:kotlinx-serialization-json-okio:1.6.0"
        const val OKIO = "com.squareup.okio:okio:3.6.0"
    }

    object Network {
        const val KTOR = "io.ktor:ktor-client-core:2.3.5"
    }

    object TestRuntime {
        const val ANDROID_JUNIT_4 = "androidx.test:runner:1.5.2"
        const val JUNIT_5 = "org.junit.jupiter:junit-jupiter-engine:5.10.0"
    }

    object TestFramework {
        const val JUNIT_4 = "junit:junit:4.13.2"
        const val JUNIT_5 = "org.junit.jupiter:junit-jupiter-api:5.10.0"
        const val ANDROID_JUNIT = "androidx.test.ext:junit:1.1.5"
    }

    object TestUtility {
        const val REACTIVE_EXTENSIONS_TEST = "com.badoo.reaktive:reaktive-testing:1.3.0"
        const val KOTEST_ASSERTIONS = "io.kotest:kotest-assertions-core:5.7.2"
        const val ESPRESSO = "androidx.test.espresso:espresso-core:3.5.1"
        const val ROBOLECTRIC = "org.robolectric:robolectric:4.11.1"
    }

    object TestDouble {
        const val MOCKK = "io.mockk:mockk:1.13.8"
        const val OKIO_FAKE_FS = "com.squareup.okio:okio-fakefilesystem:3.6.0"
        const val KTOR_MOCK_ENGINE = "io.ktor:ktor-client-mock:2.3.5"
    }
}


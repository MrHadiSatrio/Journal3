@file:Suppress("unused", "ClassName")

object Dependencies {

    object AndroidSdk {
        const val MINIMUM = 23
        const val COMPILE = 33
        const val TARGET = 32
    }

    object AndroidArchitecture {
        const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1"
        const val LIFECYCLE = "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1"
        const val STARTUP = "androidx.startup:startup-runtime:1.1.1"
    }

    object AndroidAsynchrony {
        const val WORKMANAGER = "androidx.work:work-runtime-ktx:2.8.1"
    }

    object AndroidCompatibility {
        const val CORE_KTX = "androidx.core:core-ktx:1.10.1"
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.4.2"
    }

    object AndroidNetwork {
        const val KTOR = "io.ktor:ktor-client-android:2.3.0"
    }

    object AndroidUi {
        const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.3.0"
        const val MATERIAL = "com.google.android.material:material:1.9.0"
        const val FLOW_BINDING = "io.github.reactivecircus.flowbinding:flowbinding-android:1.2.0"
    }

    object AndroidPerformance {
        const val MICROBENCHMARK = "androidx.benchmark:benchmark-junit4:1.1.1"
    }

    object AndroidSecurity {
        const val ASSENT = "com.afollestad.assent:core:3.0.2"
    }

    object Asynchrony {
        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2"
    }

    object Commons {
        const val UUID = "com.benasher44:uuid:0.7.0"
        const val DATETIME = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
        const val KOTLINX_JSON_OKIO = "org.jetbrains.kotlinx:kotlinx-serialization-json-okio:1.5.1"
        const val OKIO = "com.squareup.okio:okio:3.3.0"
    }

    object Network {
        const val KTOR = "io.ktor:ktor-client-core:2.3.0"
    }

    object TestRuntime {
        const val ANDROID_JUNIT_4 = "androidx.test:runner:1.5.2"
        const val JUNIT_5 = "org.junit.jupiter:junit-jupiter-engine:5.8.2"
    }

    object TestFramework {
        const val JUNIT_4 = "junit:junit:4.13.2"
        const val JUNIT_5 = "org.junit.jupiter:junit-jupiter-api:5.8.2"
        const val ANDROID_JUNIT = "androidx.test.ext:junit:1.1.5"
    }

    object TestUtility {
        const val COROUTINES_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.2"
        const val KOTEST_ASSERTIONS = "io.kotest:kotest-assertions-core:5.6.2"
        const val ESPRESSO = "androidx.test.espresso:espresso-core:3.5.1"
        const val ROBOLECTRIC = "org.robolectric:robolectric:4.10.2"
    }

    object TestDouble {
        const val MOCKK = "io.mockk:mockk:1.13.5"
        const val OKIO_FAKE_FS = "com.squareup.okio:okio-fakefilesystem:3.3.0"
        const val KTOR_MOCK_ENGINE = "io.ktor:ktor-client-mock:2.3.0"
    }
}


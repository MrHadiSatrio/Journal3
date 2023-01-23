package plugin

import Dependencies
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

internal fun Project.configureAndroid() {

    this.extensions.getByType<BaseExtension>().run {
        defaultConfig {
            consumerProguardFiles("$rootDir/proguard/proguard-rules.pro")
            testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
        }
    }

    this.extensions.getByType<BaseAppModuleExtension>().run {
        defaultConfig {
            minSdk = 21
            compileSdk = 33
            targetSdk = 33
        }

        sourceSets {
            getByName("main").java.srcDir("src/main/kotlin")
            getByName("test").java.srcDir("src/test/kotlin")
            getByName("androidTest").java.srcDir("src/androidTest/kotlin")
        }

        buildTypes.getByName("debug") {
            isTestCoverageEnabled = true
            isDebuggable = true
        }

        buildTypes.getByName("release") {
            isTestCoverageEnabled = false
            isDebuggable = false
        }

        buildFeatures {
            compose = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        composeOptions {
            kotlinCompilerExtensionVersion = "1.3.1"
        }

        lintOptions {
            isAbortOnError = true
        }

        testOptions {
            animationsDisabled = true
            unitTests {
                isIncludeAndroidResources = true
            }
        }

        dependencies {
            add("implementation", Dependencies.AndroidArchitecture.VIEWMODEL)
            add("implementation", Dependencies.AndroidArchitecture.LIFECYCLE)
            add("implementation", Dependencies.AndroidCompatibility.APPCOMPAT)
            add("implementation", Dependencies.AndroidCompatibility.CORE_KTX)
            add("implementation", Dependencies.AndroidUi.RECYCLER_VIEW)
            add("implementation", Dependencies.AndroidUi.MATERIAL)
            add("implementation", platform(Dependencies.AndroidUi.COMPOSE_BOM))
            add("implementation", Dependencies.AndroidUi.COMPOSE_MATERIAL_3)
            add("implementation", Dependencies.AndroidUi.COMPOSE_ACTIVITY)
            add("implementation", Dependencies.Asynchrony.COROUTINES)

            add("androidTestImplementation", Dependencies.TestDouble.MOCKK)
            add("androidTestImplementation", Dependencies.TestRuntime.ANDROID_JUNIT_4)
            add("androidTestImplementation", Dependencies.TestFramework.ANDROID_JUNIT)
            add("androidTestImplementation", Dependencies.TestUtility.ESPRESSO)
            add("androidTestImplementation", platform(Dependencies.AndroidUi.COMPOSE_BOM))
        }
    }
}

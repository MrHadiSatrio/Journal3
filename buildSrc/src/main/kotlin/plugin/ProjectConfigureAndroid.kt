package plugin

import Dependencies
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroid() {

    this.extensions.getByType<BaseExtension>().run {

        compileSdkVersion(Dependencies.AndroidSdk.COMPILE)
        buildToolsVersion("7.2.1")

        defaultConfig {
            minSdk = Dependencies.AndroidSdk.MINIMUM
            targetSdk = Dependencies.AndroidSdk.TARGET
            consumerProguardFiles("$rootDir/proguard/proguard-rules.pro")
            testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
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

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
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

            add("androidTestImplementation", Dependencies.TestDouble.MOCKK)
            add("androidTestImplementation", Dependencies.TestRuntime.ANDROID_JUNIT_4)
            add("androidTestImplementation", Dependencies.TestFramework.ANDROID_JUNIT)
            add("androidTestImplementation", Dependencies.TestUtility.ESPRESSO)
        }
    }
}

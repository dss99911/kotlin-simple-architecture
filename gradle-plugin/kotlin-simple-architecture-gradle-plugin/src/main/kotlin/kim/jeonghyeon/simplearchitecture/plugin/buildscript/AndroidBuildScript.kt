package kim.jeonghyeon.simplearchitecture.plugin.buildscript

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import kim.jeonghyeon.simplearchitecture.plugin.VERSION_COMPOSE
import kim.jeonghyeon.simplearchitecture.plugin.util.androidExtension
import kim.jeonghyeon.simplearchitecture.plugin.util.hasAndroid
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.applyAndroid() {
    if (!hasAndroid) return

    //todo how to set version?
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "kotlin-android-extensions")//for @Parcelize
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "androidx.navigation.safeargs")

    androidExtension!!.initDefault()
}

fun BaseExtension.initDefault() {

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    if (this is CommonExtension<*, *, *, *, *, *, *, *>) {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerVersion = "1.3.70-dev-withExperimentalGoogleExtensions-20200424"
            kotlinCompilerExtensionVersion = VERSION_COMPOSE
        }
    }


    //todo is it fine?
    // "More than one file was found with OS independent path 'META-INF/ktor-client-serialization.kotlin_module"
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }

    sourceSets {
        val sharedTestDir = "src/sharedTest/java"

        getByName("test") {
            java.srcDir(sharedTestDir)
        }
        getByName("androidTest") {
            java.srcDir(sharedTestDir)
        }
    }
}
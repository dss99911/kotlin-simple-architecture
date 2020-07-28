package kim.jeonghyeon.simplearchitecture.plugin.config

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import kim.jeonghyeon.simplearchitecture.plugin.VERSION_COMPOSE
import kim.jeonghyeon.simplearchitecture.plugin.extension.simpleArchExtension
import kim.jeonghyeon.simplearchitecture.plugin.util.androidExtension
import kim.jeonghyeon.simplearchitecture.plugin.util.hasAndroid
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.applyAndroidConfig() {
    afterEvaluate {
        if (!project.simpleArchExtension.androidConfig) {
            return@afterEvaluate
        }

        if (!hasAndroid) return@afterEvaluate

        //todo how to set version?
        apply(plugin = "kotlinx-serialization")
        apply(plugin = "kotlin-android-extensions")//for @Parcelize
        apply(plugin = "org.jetbrains.kotlin.kapt")

        androidExtension!!.initDefault()
    }

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
    if (this is BaseAppModuleExtension) {
        //this removes files which related kotlin feature like global property, extension functions on library build.
        //so, not remove on library
        packagingOptions {
            exclude("META-INF/*.kotlin_module")
        }
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
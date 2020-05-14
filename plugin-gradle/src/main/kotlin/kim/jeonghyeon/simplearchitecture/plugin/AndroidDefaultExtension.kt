package kim.jeonghyeon.simplearchitecture.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.applyAndroid() {
    if (!hasAndroid) return

    //todo check if this is working
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "kotlin-android-extensions")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "androidx.navigation.safeargs")

    androidExtension!!.initDefault()
}

fun BaseExtension.initDefault() {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    dataBinding {
        isEnabled = true
        isEnabledForTests = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    //todo is it fine?
    // "More than one file was found with OS independent path 'META-INF/ktor-client-serialization.kotlin_module"
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }

    sourceSets {
        val sharedTestDir = "src/androidSharedTest/kotlin"

        getByName("test") {
            java.srcDir(sharedTestDir)
        }
        getByName("androidTest") {
            java.srcDir(sharedTestDir)
        }

        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/java", "src/androidMain/kotlin")
            res.srcDirs("src/androidMain/res")
        }
    }
}
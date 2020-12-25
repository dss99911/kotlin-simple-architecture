plugins {
    //android
    id("com.android.library")

    //common
    kotlin("multiplatform")

    //native
    kotlin("native.cocoapods")
}

apply(plugin = "kim.jeonghyeon.kotlin-simple-architecture-gradle-plugin")


version = "1.0"//for cocoa pod

kotlin {
    android()

    ios()

    jvm()//for backend

    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "Template"
        homepage = "https://github.com/dss99911/kotlin-simple-architecture-template"
        podfile = project.file("../ios/Podfile")

    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(deps.simpleArch.common)
            }
        }
        val clientMain by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(clientMain)
        }

        val androidDebug by getting {

        }

        val iosMain by getting {
            dependsOn(clientMain)
        }
    }
}


android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
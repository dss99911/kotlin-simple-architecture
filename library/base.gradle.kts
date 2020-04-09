//plugins {
//    //    id("kotlin-android")
//    id("kotlin-multiplatform") version versions.kotlin.version
//    id("kotlin-android-extensions")
//    id("kotlinx-serialization") version versions.kotlin.version
////    id("kotlinx-kapt")
//    kotlin("kapt")
//    id("androidx.navigation.safeargs")
//}
//
//val androidKeyAlias: String by project
//val androidKeyPassword: String by project
//val androidStoreFile: String by project
//val androidStorePassword: String by project
//
//val compileSdkVersion: Int by project
//val buildToolVersion: String by project
//val minVersion: Int by project
//val targetVersion: Int by project
//
//android {
//
//    compileSdkVersion(compileSdkVersion)
//    buildToolsVersion(buildToolVersion)
//    defaultConfig {
//        minSdkVersion(minVersion)
//        targetSdkVersion(targetVersion)
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        multiDexEnabled = true
//
//        vectorDrawables.useSupportLibrary = true
//
//        buildConfigField("String", "freePackageName", "\"${config.applicationId}\"")
//
//        buildConfigField("boolean", "isFree", "false")
//        buildConfigField("boolean", "isPro", "false")
//        buildConfigField("boolean", "isDev", "false")
//        buildConfigField("boolean", "isProd", "false")
//        buildConfigField("boolean", "isMock", "false")
//    }
//
//    flavorDimensions("mode", "stage")
//
//    val FLAVOR_NAME_MOCK = "mock"
//
//    productFlavors {
//        val free by creating {
//            setDimension("mode")
//            buildConfigField("boolean", "isFree", "true")
//        }
//
//        val pro by creating {
//            setDimension("mode")
//            applicationIdSuffix = (applicationIdSuffix ?: "") + ".pro"
//            buildConfigField("boolean", "isPro", "true")
//        }
//
//        val dev by creating {
//            setDimension("stage")
//            applicationIdSuffix = (applicationIdSuffix ?: "") + ".dev"
//            versionNameSuffix = "-dev"
//
//            buildConfigField("boolean", "isDev", "true")
//            //optimize build time
//            resConfigs("en", "hdpi")
//            minSdkVersion(if (minVersion > 21) minVersion else 21)
//        }
//
//        val prod by creating {
//            setDimension("stage")
//            buildConfigField("boolean", "isProd", "true")
//        }
//
//        create(FLAVOR_NAME_MOCK) {
//            setDimension("stage")
//
//            applicationIdSuffix = (applicationIdSuffix ?: "") + ".mock"
//            versionNameSuffix = "-mock"
//
//            buildConfigField("boolean", "isMock", "true")
//            //optimize build time
//            resConfigs("en", "hdpi")
//            minSdkVersion(if (minVersion > 21) minVersion else 21)
//        }
//    }
//
//    val SIGNING_CONFIG_NAME = "release"
//
//    signingConfigs {
//        create(SIGNING_CONFIG_NAME) {
//            keyAlias = androidKeyAlias
//            keyPassword = androidKeyPassword
//            storeFile = file(androidStoreFile)
//            storePassword = androidStorePassword
//        }
//    }
//
//    val BUILD_TYPE_NAME_DEBUG = "debug"
//    val BUILD_TYPE_NAME_RELEASE = "release"
//
//    buildTypes {
//        getByName(BUILD_TYPE_NAME_DEBUG) {
//            isTestCoverageEnabled = true
//            isTestCoverageEnabled = true
//        }
//
//        getByName(BUILD_TYPE_NAME_RELEASE) {
//            isMinifyEnabled = true
//            isShrinkResources = true
//            isZipAlignEnabled = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android.txt"),
//                "proguard-rules.pro",
//                project(":library").projectDir.toString() + "/proguard-rules.pro"
//            )
//            signingConfig = signingConfigs.getByName(SIGNING_CONFIG_NAME)
//        }
//    }
//
//    // Remove mockRelease as it's not needed.
//    android.variantFilter {
//        if (buildType.name == BUILD_TYPE_NAME_RELEASE
//            && flavors[0].name == FLAVOR_NAME_MOCK
//        ) {
//            setIgnore(true)
//        }
//    }
//
//    dataBinding {
//        isEnabled = true
//        isEnabledForTests = true
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//
//    //todo do we need this on kts? or how to do that?
////    kotlinOptions {
////        jvmTarget = "1.8"
////    }
//
//    // Always show the result of every unit test, even if it passes.
//    testOptions {
//        unitTests.isIncludeAndroidResources = true
//        animationsDisabled = true
//        //todo how to set testLogging?
////        all {
//        //                testLogging {
////                    events("passed", "skipped", "failed", "standardOut", "standardError")
////                }
////            }
//    }
//
//    sourceSets {
//        val sharedTestDir = "src/sharedTest/java"
//        val test by getting {
//            java.srcDir(sharedTestDir)
//        }
//        val androidTest by getting {
//            java.srcDir(sharedTestDir)
//        }
//    }
//
//}
//
//kotlin {
//    android()
//
//    sourceSets {
//        val androidMain by getting {
//            dependencies {
//                implementation(project(":common"))
//            }
//        }
//    }
//}
//
//dependencies {
//    implementation(project(":library"))
//    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
//    testImplementation(project(":testing"))
//    androidTestImplementation(project(":androidtesting"))
//    //todo library, androidtesting, base.gradle can be split in dependency except for this kapt. we have to consider. whether split them or make one package
//    //todo check if add("kapt","") is working or not.
//    add("kapt", "androidx.room:room-compiler:${versions.android.room}")
//}
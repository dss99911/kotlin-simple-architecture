import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//todo what is this?
val ideaActive = System.getProperty("idea.active") == "true"

//configurations {
//
//    compileClasspath
//}

plugins {
    //common
    kotlin("multiplatform")
//    id("kotlinx-serialization")

    //android
    id("com.android.application")
//    id("kotlin-android-extensions")
//    kotlin("kapt")
//    id("androidx.navigation.safeargs")
//    id("com.squareup.sqldelight")
}

apply(plugin = "kotlin-simple-architecture-gradle-plugin")

//sqldelight {
//
//    database("HockeyDb2") {
//        packageName = "com.balancehero.example1"
//    }
//}

android {
    //todo move configuration on plugin
    val androidKeyAlias: String by project
    val androidKeyPassword: String by project
    val androidStoreFile: String by project
    val androidStorePassword: String by project

    val appId = "kim.jeonghyeon.sample"

    compileSdkVersion(config.compileSdkVersion)
    buildToolsVersion(config.buildToolVersion)
    defaultConfig {
        versionCode = 10000
        versionName = "1.00.00"
        minSdkVersion(config.minSdkVersion)
        targetSdkVersion(config.targetSdkVersion)

        buildConfigField("String", "freePackageName", "\"${appId}\"")

        buildConfigField("boolean", "isFree", "false")
        buildConfigField("boolean", "isPro", "false")
        buildConfigField("boolean", "isDev", "false")
        buildConfigField("boolean", "isProd", "false")
        buildConfigField("boolean", "isMock", "false")
    }

    flavorDimensions("mode", "stage")

    val FLAVOR_NAME_MOCK = "mock"

    productFlavors {
        val free by creating {
            dimension = "mode"
            applicationId = appId
            buildConfigField("boolean", "isFree", "true")
        }

        val pro by creating {
            dimension = "mode"
            applicationId = appId + ".pro"
            buildConfigField("boolean", "isPro", "true")
        }

        val dev by creating {
            dimension = "stage"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            buildConfigField("boolean", "isDev", "true")
            //optimize build time
            resConfigs("en", "hdpi")
            minSdkVersion(if (config.minSdkVersion > 21) config.minSdkVersion else 21)
        }

        val prod by creating {
            dimension = "stage"
            buildConfigField("boolean", "isProd", "true")
        }

        create(FLAVOR_NAME_MOCK) {
            dimension = "stage"

            applicationIdSuffix = ".mock"
            versionNameSuffix = "-mock"

            buildConfigField("boolean", "isMock", "true")
            //optimize build time
            resConfigs("en", "hdpi")
            minSdkVersion(if (config.minSdkVersion > 21) config.minSdkVersion else 21)
        }
    }

    val SIGNING_CONFIG_NAME = "release"

    signingConfigs {
        create(SIGNING_CONFIG_NAME) {
            keyAlias = androidKeyAlias
            keyPassword = androidKeyPassword
            storeFile = file(androidStoreFile)
            storePassword = androidStorePassword
        }
    }

    val BUILD_TYPE_NAME_DEBUG = "debug"
    val BUILD_TYPE_NAME_RELEASE = "release"

    buildTypes {
        getByName(BUILD_TYPE_NAME_DEBUG) {
            isTestCoverageEnabled = true
        }

        getByName(BUILD_TYPE_NAME_RELEASE) {
            isMinifyEnabled = true
            isShrinkResources = true
            isZipAlignEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName(SIGNING_CONFIG_NAME)
        }
    }

    // Remove mockRelease as it's not needed.
    android.variantFilter {
        if (buildType.name == BUILD_TYPE_NAME_RELEASE && flavors[0].name == FLAVOR_NAME_MOCK) {
            setIgnore(true)
        }
    }

    // Always show the result of every unit test, even if it passes.
    testOptions {
        unitTests.isIncludeAndroidResources = true
        animationsDisabled = true

//        todo how to set testLogging?
//        all {
//            testLogging {
//                events("passed", "skipped", "failed", "standardOut", "standardError")
//            }
//        }
    }

}

kotlin {
    jvm()
    android()

    val iosArm32 = iosArm32("iosArm32")
    val iosArm64 = iosArm64("iosArm64")
    val iosX64 = iosX64("iosX64")

    if (ideaActive) {
        iosX64("ios")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kotlin-simple-architecture"))
            }
        }
        //TODO HYUN [multi-platform2] : consider to change to clientMain. as front end also may be included to here
        val mobileMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {}

        val androidMain by getting {
            dependsOn(mobileMain)
            dependsOn(jvmMain)

            dependencies {
                //todo after upgrade kotlin from 1.3.61 to 1.3.71. there were the build error below
                //it's strange. let's try after moving to multimplatform module
                /** https://github.com/google/dagger/issues/95
                 * /Users/hyun.kim/AndroidstudioProjects/my/androidLibrary/sample/build/generated/source/kapt/freeDevDebug/androidx/databinding/library/baseAdapters/BR.java:5: error: cannot find symbol
                @Generated("Android Data Binding")
                ^
                symbol: class Generated
                 */
                implementation("org.glassfish:javax.annotation:10.0-b28")
            }
        }

        val iosMain = if (ideaActive) {
            getByName("iosMain")
        } else {
            create("iosMain")
        }

        iosMain.apply {
            dependsOn(mobileMain)
        }

        val iosArm32Main by getting {}
        val iosArm64Main by getting {}
        val iosX64Main by getting {}

        configure(listOf(iosArm32Main, iosArm64Main, iosX64Main)) {
            dependsOn(iosMain)
        }
    }

    val frameworkName = "KotlinApi"

    configure(listOf(iosArm32, iosArm64, iosX64)) {
        compilations {
            val main by getting {
                //for supporting kotlin generic in swift. there were discussion that it'll be applied on kotlin 1.3.40. let's see if this script is required or not.
//                extraOpts("-Xobjc-generics")
            }
        }

        binaries.framework {
            export(deps.kotlin.coroutineCoreCommon)
            //native will use this name to refer the multiplatform library
            baseName = frameworkName
        }
    }

    tasks.register<FatFrameworkTask>("debugFatFramework") {
        baseName = frameworkName
        group = "Universal framework"
        description = "Builds a universal (fat) debug framework"

        from(iosX64.binaries.getFramework("DEBUG"))
    }

    tasks.register<FatFrameworkTask>("releaseFatFramework") {
        baseName = frameworkName
        group = "Universal framework"
        description = "Builds a universal (release) debug framework"

        from(iosArm64.binaries.getFramework("RELEASE"), iosArm32.binaries.getFramework("RELEASE"))
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}


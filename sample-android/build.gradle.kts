import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("kotlin-multiplatform")
    id("kotlin-android-extensions")
    id("kotlinx-serialization")
    kotlin("kapt")
    id("androidx.navigation.safeargs")
//    id("com.squareup.sqldelight")
}

val androidKeyAlias: String by project
val androidKeyPassword: String by project
val androidStoreFile: String by project
val androidStorePassword: String by project

val appId = "kim.jeonghyeon.sample"

//sqldelight {
//
//    database("HockeyDb2") {
//        packageName = "com.balancehero.example1"
//    }
//}

android {

    compileSdkVersion(config.compileSdkVersion)
    buildToolsVersion(config.buildToolVersion)
    defaultConfig {
        versionCode = 10000
        versionName = "1.00.00"
        minSdkVersion(config.minSdkVersion)
        targetSdkVersion(config.targetSdkVersion)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true

        vectorDrawables.useSupportLibrary = true

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
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro",
                project(":library").projectDir.toString() + "/proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName(SIGNING_CONFIG_NAME)
        }
    }

    // Remove mockRelease as it's not needed.
    android.variantFilter {
        if (buildType.name == BUILD_TYPE_NAME_RELEASE
            && flavors[0].name == FLAVOR_NAME_MOCK
        ) {
            setIgnore(true)
        }
    }

    dataBinding {
        isEnabled = true
        isEnabledForTests = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Always show the result of every unit test, even if it passes.
    testOptions {
        unitTests.isIncludeAndroidResources = true
        animationsDisabled = true
        //todo how to set testLogging?
//        all {
        //                testLogging {
//                    events("passed", "skipped", "failed", "standardOut", "standardError")
//                }
//            }
    }

    sourceSets {
        val sharedTestDir = "src/sharedTest/java"
        val test by getting {
            java.srcDir(sharedTestDir)
            java.srcDir(project(":library").projectDir.toString() + "/src/test/java")
        }
        val androidTest by getting {
            java.srcDir(sharedTestDir)
            java.srcDir(project(":library").projectDir.toString() + "/src/androidTest/java")
        }
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    android()

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":kotlin-simple-architecture"))
            }
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    testImplementation(deps.simpleArch.androidTest)
    androidTestImplementation(deps.simpleArch.androidAndroidTest)

    implementation("com.android.support:customtabs:23.3.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.2.0")//todo Rxjava3 released, but adapter seems not exsits yet.

    if (config.useLeakCanary) {
        debugImplementation("com.squareup.leakcanary:leakcanary-android:2.0")
    }
//    debugImplementation("com.facebook.stetho:stetho:1.5.1")
    implementation("com.squareup.sqldelight:android-driver:1.3.0")
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
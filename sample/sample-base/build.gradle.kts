import kim.jeonghyeon.simplearchitecture.plugin.task.PROPERTY_NAME_BUILD_TIME_LOCAL_IP_ADDRESS
import kim.jeonghyeon.simplearchitecture.plugin.task.simpleProperty

val androidKeyAlias: String by project
val androidKeyPassword: String by project
val androidStoreFile: String by project
val androidStorePassword: String by project

plugins {
    //android
    //todo when removing sample-android. change to com.android.application
    id("com.android.library")

    //common
    kotlin("multiplatform")

    //native
    kotlin("native.cocoapods")
}

//server url can be changed for different environment, so defined here
val serverUrl = "https://sample.jeonghyeon.kim"

//we have to define deeplink path on AndroidManifest.xml
//so, defined deeplink here
val deeplinkScheme = "kim.jeonghyeon.kotlinios"
val deeplinkHost = "sample.jeonghyeon.kim"
val deeplinkPrePath = "/deeplink"

simpleArch {
    //todo how to set environment on cocoapod?
    val isProduction by simpleProperty(false/*getEnvironment() == "production"*/)
    val deeplinkScheme by simpleProperty(deeplinkScheme)
    val deeplinkHost by simpleProperty(deeplinkHost)
    val deeplinkPrePath by simpleProperty(deeplinkPrePath)
    simpleProperties["serverUrl"] = if (isProduction) {
        "\"$serverUrl\""
    } else {
        "\"http://\$$PROPERTY_NAME_BUILD_TIME_LOCAL_IP_ADDRESS:8080\""
    }
}

apply(plugin = deps.simpleArch.gradle.toPlugInId())

sqldelight {

    database("SampleDb") {
        packageName = "kim.jeonghyeon.sample"
    }
}


version = "1.0"//for cocoa pod

kotlin {
    jvm()

    android()

    //todo make this simpler by cocoa?
    ios {
        //todo how to export these?
//        binaries.framework {
//            export(deps.kotlin.coroutineCore)
//            export(deps.simpleArch.common)
//            //native will use this name to refer the multiplatform library
//        }
    }

    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "Sample"
        homepage = "https://github.com/dss99911/kotlin-simple-architecture"
        podfile = project.file("../sample-native/Podfile")

    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(deps.simpleArch.client)
            }
        }
        //TODO HYUN [multi-platform2] : consider to change to clientMain. as front end also may be included to here
        val mobileMain by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(mobileMain)

            dependencies {
                implementation("com.squareup.retrofit2:retrofit:2.9.0")
                implementation("com.squareup.retrofit2:converter-gson:2.9.0")
                implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
                implementation(deps.ktor.clientOkHttp)
                implementation(deps.ktor.clientGson)

            }
        }

        val iosMain by getting {
            dependsOn(mobileMain)
        }
    }

//    //https://github.com/cashapp/sqldelight/issues/1442
//    todo error when use the code below
//     but working with template project. check the difference
//    targets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().forEach{
//        it.binaries.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.Framework>()
//            .forEach { lib ->
//                lib.isStatic = false
//                lib.linkerOpts.add("-lsqlite3")
//            }
//    }
}


android {
    val appId = "kim.jeonghyeon.sample"

    compileSdkVersion(config.compileSdkVersion)
    buildToolsVersion(config.buildToolVersion)
    defaultConfig {
        versionCode = 10000
        versionName = "1.00.00"
        minSdkVersion(config.minSdkVersion)
        targetSdkVersion(config.targetSdkVersion)


        //todo this is only for android, is this required?
        buildConfigField("String", "freePackageName", "\"${appId}\"")

        //todo this is only for android, is this required?
        buildConfigField("boolean", "isFree", "false")
        buildConfigField("boolean", "isPro", "false")
        buildConfigField("boolean", "isDev", "false")
        buildConfigField("boolean", "isProd", "false")
        buildConfigField("boolean", "isMock", "false")

        resValue("string", "deeplink_scheme", deeplinkScheme)
        resValue("string", "deeplink_host", deeplinkHost)
        resValue("string", "deeplink_pathPrefix", deeplinkPrePath)
    }

    flavorDimensions("mode", "stage")

    val FLAVOR_NAME_MOCK = "mock"

    productFlavors {
        val free by creating {
            dimension = "mode"
            //todo when removing sampleandroid. uncomment this
            //applicationId = appId
            buildConfigField("boolean", "isFree", "true")
        }

        val pro by creating {
            dimension = "mode"
            //todo when removing sampleandroid. uncomment this
            //applicationId = appId + ".pro"
            buildConfigField("boolean", "isPro", "true")
        }

        //todo environment is managed by gradle property, is this required?
        // and change name 'environment' to 'stage'?
        val dev by creating {
            dimension = "stage"
            //todo when removing sampleandroid. uncomment this
//            applicationIdSuffix = ".dev"
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

            //todo when removing sampleandroid. uncomment this
//            applicationIdSuffix = ".mock"
            versionNameSuffix = "-mock"

            buildConfigField("boolean", "isMock", "true")
            //optimize build time
            resConfigs("en", "hdpi")
            minSdkVersion(if (config.minSdkVersion > 21) config.minSdkVersion else 21)
        }
    }

    val SIGNING_CONFIG_NAME_RELEASE = "release"

    signingConfigs {
        create(SIGNING_CONFIG_NAME_RELEASE) {
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
            //todo when removing sampleandroid. uncomment this
//            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName(SIGNING_CONFIG_NAME_RELEASE)
        }
    }

    // Always show the result of every unit test, even if it passes.
    testOptions {
        unitTests.isIncludeAndroidResources = true
        animationsDisabled = true
    }

}


tasks.register<Exec>("showKeyDetail") {
    commandLine(
        "sh",
        "-c",
        "keytool -list -v -keystore $androidStoreFile -alias $androidKeyAlias -storepass $androidStorePassword -keypass $androidKeyPassword"
    )
}
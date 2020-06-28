import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ideaActive = System.getProperty("idea.active") == "true"

plugins {
    `maven-publish`
    id("com.android.library")
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("kotlin-android-extensions")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs")
}

group = deps.simpleArch.common.getGroupId()
version = deps.simpleArch.common.getVersion()

kotlin {

    //this is used for server backend.
    jvm()

    android {
        publishLibraryVariants("release", "debug")
    }

    js()

    val iosArm32 = iosArm32("iosArm32")
    val iosArm64 = iosArm64("iosArm64")
    val iosX64 = iosX64("iosX64")

    if (ideaActive) {
        iosX64("ios")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(deps.kotlin.stdlib)
                api(deps.kotlin.coroutineCoreCommon)
                api(deps.kotlin.serializationRuntimeCommon)
                api(deps.ktor.clientCore)
                api(deps.ktor.clientSerialization)
                api(deps.ktor.clientLogging)
                api(deps.sqldelight.runtime)
                api(deps.sqldelight.coroutine)
            }
        }
        //TODO HYUN [multi-platform2] : consider to change to clientMain. as front end also may be included to here
        val mobileMain by creating {
            dependsOn(commonMain)
        }

        val jvmShareMain by creating {
            dependencies {
                //kotlin
                api(deps.kotlin.stdlibJdk8)
                api(deps.kotlin.serializationRuntime)
                api(deps.kotlin.coroutineCore)
                api(deps.kotlin.reflect)

                //ktor client
                api(deps.ktor.clientCoreJvm)
                api(deps.ktor.clientSerializationJvm)
                api(deps.ktor.clientLoggingJvm)

                api(deps.gson)
            }
        }

        val jvmMain by getting {
            dependsOn(jvmShareMain)
            dependencies {
                api(deps.sqldelight.jvm)
            }
        }

        //todo add frontend js and backend Js
        val jsMain by getting {
            dependencies {
                api(deps.kotlin.stdlibJs)
                api(deps.kotlin.serializationRuntimeJs)
                api(deps.kotlin.coroutineCoreJs)

                api(deps.ktor.clientJs)
                api(deps.ktor.clientSerializationJs)
                api(deps.ktor.clientLoggingJs)
            }
        }

        val androidMain by getting {
            dependsOn(mobileMain)
            dependsOn(jvmShareMain)

            dependencies {
                //todo move to library

                api(deps.kotlin.coroutineAndroid)
                api(deps.ktor.clientAndroid)

                api(deps.android.appCompat)
                api(deps.android.supportCompat)
                api(deps.android.core)
                api(deps.android.vectordrawable)

                api(deps.android.material)
                api(deps.android.work)
                deps.android.compose.forEach { api(it) }

                api(deps.android.anko)

                api(deps.android.timber)
                api(deps.sqldelight.android)
            }
        }

        //when build ios specific target, it knows that this source set targets the target.
        //but, intellij doesn't know that iosMain source set targets ios or not.
        //so, when configuration is for intellij. we have to specify the target for iosMain.
        val iosMain = if (ideaActive) {
            getByName("iosMain")
        } else {
            create("iosMain")
        }

        iosMain.apply {
            dependsOn(mobileMain)

            dependencies {
                api(deps.kotlin.coroutineCoreNative)
                api(deps.kotlin.serializationRuntimeNative)

                api(deps.ktor.clientIos)
                api(deps.ktor.clientSerializationNative)
                api(deps.ktor.clientLoggingNative)
                api(deps.sqldelight.native)
            }
        }

        val iosArm32Main by getting {}
        val iosArm64Main by getting {}
        val iosX64Main by getting {}

        configure(listOf(iosArm32Main, iosArm64Main, iosX64Main)) {
            dependsOn(iosMain)
        }
    }
}

android {
    compileSdkVersion(config.compileSdkVersion)
    buildToolsVersion(config.buildToolVersion)
    defaultConfig {
        minSdkVersion(config.minSdkVersion)
        targetSdkVersion(config.targetSdkVersion)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerVersion = "1.3.70-dev-withExperimentalGoogleExtensions-20200424"
        kotlinCompilerExtensionVersion = versions.android.compose
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.build {
    finalizedBy(tasks.publishToMavenLocal)
}
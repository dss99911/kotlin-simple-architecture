import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//todo what is this?
val ideaActive = System.getProperty("idea.active") == "true"

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("kotlinx-serialization")
}

android {
    compileSdkVersion(28)
    buildToolsVersion = "29.0.2"
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)
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
                implementation(deps.kotlin.stdlib)
                implementation(deps.kotlin.coroutineCoreCommon)
                implementation(deps.kotlin.serializationRuntimeCommon)

                implementation(deps.ktor.clientCore)
                implementation(deps.ktor.clientSerialization)
                implementation(deps.ktor.clientLogging)
            }
        }
        //TODO HYUN [multi-platform2] : consider to change to clientMain. as front end also may be included to here
        val mobileMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependencies {
                api(deps.simpleArch.jvm)
                //todo hyun : remove this after jvm library update
                api(deps.ktor.clientGson)
                api(deps.ktor.clientLoggingJvm)
            }
        }

        val androidMain by getting {
            dependsOn(mobileMain)
            dependsOn(jvmMain)

            dependencies {
                api(deps.simpleArch.android)
            }
        }

        val iosMain = if (ideaActive) {
            getByName("iosMain")
        } else {
            create("iosMain")
        }

        iosMain.apply {
            dependsOn(mobileMain)

            dependencies {
                implementation(deps.kotlin.coroutineCoreNative)
                implementation(deps.kotlin.serializationRuntimeNative)
                
                implementation(deps.ktor.clientIos)
                implementation(deps.ktor.clientSerializationNative)
            }
        }

        val iosArm32Main by getting
        val iosArm64Main by getting
        val iosX64Main by getting

        configure(listOf(iosArm32Main, iosArm64Main, iosX64Main)) {
            dependsOn(iosMain)
        }
    }

    //todo what is this?
    val frameworkName = "What is this?"

    configure(listOf(iosArm32, iosArm64, iosX64)) {
        compilations {
            val main by getting {
                extraOpts("-Xobjc-generics")
            }
        }

        binaries.framework {
            export(deps.kotlin.coroutineCoreCommon)
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

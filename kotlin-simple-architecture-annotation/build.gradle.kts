plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = deps.simpleArch.annotation.getGroupId()
version = deps.simpleArch.annotation.getVersion()

kotlin {
    //if this multiplatform doesn't include any platform that your project is using, then your project won't recognize this library
    jvm()
    js()

    //todo remove on 1.4 and add ios()
    val iosArm32 = iosArm32("iosArm32")
    val iosArm64 = iosArm64("iosArm64")
    val iosX64 = iosX64("iosX64")

    //todo is this required? try to remove
    android {
        publishLibraryVariants("release", "debug")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(deps.kotlin.stdlib)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(deps.kotlin.stdlibJdk8)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(deps.kotlin.stdlibJs)
            }
        }
    }
}

android {
    compileSdkVersion(config.compileSdkVersion)
    buildToolsVersion(config.buildToolVersion)
    defaultConfig {
        minSdkVersion(config.minSdkVersion)
        targetSdkVersion(config.targetSdkVersion)

    }
}

publishMPP()
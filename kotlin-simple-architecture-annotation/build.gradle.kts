plugins {
    kotlin("multiplatform")
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(deps.kotlin.stdlibCommon)
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

publishMPP()
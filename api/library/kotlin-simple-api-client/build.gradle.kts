buildscript {
    repositories {
        mavenLocal()
        google()
        jcenter()
    }

    dependencies {
        classpath(deps.simpleArch.api.gradle)
    }
}

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

apply(plugin = "com.squareup.sqldelight")
apply(plugin = "kotlinx-serialization")
simpleArch {
    postfix = "simple"
}
apply(plugin = deps.simpleArch.api.gradle.toPlugInId())

group = deps.simpleArch.api.client.getGroupId()
version = deps.simpleArch.api.client.getVersion()

sqldelight {
    database("SimpleDB") {
        packageName = "kim.jeonghyeon.db"
    }
}

kotlin {

    jvm()

    android {
        publishLibraryVariants("release", "debug")
    }

    js().browser()

    ios()


    sourceSets {
        val commonMain by getting {
            dependencies {
                api(deps.kotlin.coroutineCore)
                api(deps.kotlin.serializationCore)
                api(deps.kotlin.reflect)

                api(deps.ktor.clientCore)
                api(deps.ktor.clientSerialization)
                api(deps.ktor.clientLogging)
                api(deps.ktor.clientAuth)

                api(deps.sqldelight.runtime)
                api(deps.sqldelight.coroutine)

                api(deps.krypto)

                api(deps.simpleArch.api.annotation)
            }
        }

        val jvmShareMain by creating {
            dependencies {
                api(deps.gson)
            }
        }

        val jvmMain by getting {
            dependsOn(jvmShareMain)
            dependencies {

                api(deps.sqldelight.jvm)
            }
        }


        val jsMain by getting {
            dependencies {
                api(deps.ktor.clientJs)
            }
        }

        val androidMain by getting {
            dependsOn(jvmShareMain)
            dependencies {
                api(deps.ktor.clientAndroid)
                api(deps.sqldelight.android)
                api(deps.android.timber)
            }
        }

        val iosMain by getting {
            dependencies {
                api(deps.ktor.clientIos)
                api(deps.sqldelight.native)
            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
}

android {
    compileSdkVersion(config.compileSdkVersion)
    buildToolsVersion(config.buildToolVersion)
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(config.targetSdkVersion)
    }

}

publishMPP()
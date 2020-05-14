import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//todo what is this?
val ideaActive = System.getProperty("idea.active") == "true"


plugins {
    `maven-publish`
    id("com.android.library")
    kotlin("multiplatform")
    id("kotlinx-serialization")
//    id("com.squareup.sqldelight")
}

group = "kim.jeonghyeon"
version = "0.0.1"

//sqldelight {
//
//    database("HockeyDb") {
////        sourceSet = files("src/commonMain/sqldelight")
//        packageName = "com.balancehero.example1"
//    }
//}


android {
    compileSdkVersion(28)
    buildToolsVersion = "29.0.2"
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/java", "src/androidMain/kotlin")
            res.srcDirs("src/androidMain/res")
        }
    }
}

kotlin {
    jvm()
    android {
        publishLibraryVariants("release", "debug")
    }

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
            }
        }
        //TODO HYUN [multi-platform2] : consider to change to clientMain. as front end also may be included to here
        val mobileMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependencies {
                //todo hyun : remove this after jvm library update
                api(deps.ktor.clientSerializationJvm)
                api(deps.ktor.clientLoggingJvm)

                api(deps.kotlin.stdlibJdk8)
                api(deps.ktor.clientGson)
                api(deps.kotlin.serializationRuntime)
                api(deps.kotlin.coroutineCore)
                api(deps.kotlin.reflect)
                api(deps.ktor.clientCoreJvm)
                api(deps.gson)
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
                api(deps.kotlin.coroutineCoreNative)
                api(deps.kotlin.serializationRuntimeNative)

                api(deps.ktor.clientIos)
                api(deps.ktor.clientSerializationNative)
                api(deps.ktor.clientLoggingNative)
            }
        }

        val iosArm32Main by getting {}
        val iosArm64Main by getting {}
        val iosX64Main by getting {}

        configure(listOf(iosArm32Main, iosArm64Main, iosX64Main)) {
            dependsOn(iosMain)
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}


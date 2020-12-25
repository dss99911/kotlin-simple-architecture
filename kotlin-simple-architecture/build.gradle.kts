buildscript {
    repositories {
        mavenLocal()
        google()
        jcenter()
    }

    dependencies {
        classpath(deps.simpleArch.pluginGradle)
    }
}

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

simpleArch {
    postfix = "simple"
    simpleConfig = false
}
apply(plugin = "kim.jeonghyeon.kotlin-simple-architecture-gradle-plugin")

group = deps.simpleArch.common.getGroupId()
version = deps.simpleArch.common.getVersion()

sqldelight {
    database("SimpleDB") {
        packageName = "kim.jeonghyeon.db"
    }
}

kotlin {

    //this is used for server backend.
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
                api(deps.sqldelight.runtime)
                api(deps.sqldelight.coroutine)
                api(deps.simpleArch.annotation)
                api(deps.ktor.clientAuth)
                api(deps.krypto)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(deps.kotlin.testCommon)
                implementation(deps.kotlin.testAnnotationCommon)
            }
        }

        //TODO HYUN [multi-platform2] : consider to change to clientMain. as front end also may be included to here
        val mobileMain by creating {
            dependsOn(commonMain)
        }
        val mobileTest by creating {
            dependsOn(commonTest)
        }

        val jvmShareMain by creating {
            dependencies {
                //kotlin
                api(deps.kotlin.reflect)
                api(deps.gson)
            }
        }

        val jvmShareTest by creating {
            dependencies {
                implementation(deps.kotlin.testJunit)
                implementation(deps.junit)
            }
        }


        val jvmMain by getting {
            dependsOn(jvmShareMain)
            dependencies {
                api(deps.sqldelight.jvm)
                api(deps.ktor.core)
                api(deps.ktor.auth)
                api(deps.ktor.authJwt)
                api(deps.ktor.gson)
                api(deps.ktor.serverNetty)
                api(deps.ktor.serialization)
                api(deps.logback)
                api(deps.ktor.serverSessions)
                api(deps.ktor.clientEngineApache)
            }
        }

        val jvmTest by getting {
            dependsOn(jvmShareTest)
        }

        //todo add frontend js and backend Js
        val jsMain by getting {
            dependsOn(mobileMain)
            dependencies {
                api(deps.ktor.clientJs)
            }
        }

        val androidMain by getting {
            dependsOn(mobileMain)
            dependsOn(jvmShareMain)

            dependencies {
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
                api(deps.ktor.clientAndroid)
            }
        }
        val androidTest by getting {
            dependsOn(mobileTest)
            dependsOn(jvmShareTest)
        }

        val iosMain by getting {
            dependsOn(mobileMain)

            dependencies {
                api(deps.sqldelight.native)
                api(deps.ktor.clientIos)

            }
        }

        val iosTest by getting {
            dependsOn(mobileTest)
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
        minSdkVersion(config.minSdkVersion)
        targetSdkVersion(config.targetSdkVersion)
    }

    //todo after change android build tool to 4.2.0-alpha12 error occurs, try removing this and build again
    lintOptions {
        disable("InvalidFragmentVersionForActivityResult")
    }
}

publishMPP()
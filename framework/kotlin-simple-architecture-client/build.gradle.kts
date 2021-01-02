import com.squareup.sqldelight.gradle.SqlDelightExtension

buildscript {
    repositories {
        mavenLocal()
        google()
        jcenter()
    }

    dependencies {
        classpath(deps.simpleArch.gradle)
        classpath(deps.android.buildToolGradle)
    }
}

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

simpleArch {
    postfix = "simpleFramework"
    simpleConfig = false
    isInternal = true
}
apply(plugin = deps.simpleArch.gradle.toPlugInId())

group = deps.simpleArch.client.getGroupId()
version = deps.simpleArch.client.getVersion()

val buildByLibrary: String? by project

val sqlDelight = project.extensions.getByType<SqlDelightExtension>()
sqlDelight.database("SimpleDB") {
    packageName = "kim.jeonghyeon.db"
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
                api(deps.ktor.clientSerialization)
                api(deps.ktor.clientLogging)
                api(deps.ktor.clientAuth)

                api(deps.sqldelight.runtime)
                api(deps.sqldelight.coroutine)

                if (buildByLibrary == "true") {
                    api(deps.simpleArch.api.client)
                } else {
                    api(project(":api:library:${deps.simpleArch.api.client.getArtifactId()}"))
                }

                api(deps.krypto)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(deps.kotlin.testCommon)
                implementation(deps.kotlin.testAnnotationCommon)
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

        val androidMain by getting {
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

                api(deps.sqldelight.android)
                api(deps.android.timber)
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(deps.kotlin.testJunit)
                implementation(deps.junit)
            }
        }

        val iosMain by getting {
            dependencies {
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
        minSdkVersion(config.minSdkVersion)
        targetSdkVersion(config.targetSdkVersion)
    }

    //todo after change android build tool to 4.2.0-alpha12 error occurs, try removing this and build again
    lintOptions {
        disable("InvalidFragmentVersionForActivityResult")
    }
}

publishMPP()
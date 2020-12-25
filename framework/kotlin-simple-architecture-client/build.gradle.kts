buildscript {
    repositories {
        mavenLocal()
        google()
        jcenter()
    }

    dependencies {
        classpath(deps.simpleArch.gradle)
    }
}

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

simpleArch {
    postfix = "simpleFramework"
    simpleConfig = false
}
apply(plugin = deps.simpleArch.gradle.toPlugInId())

group = deps.simpleArch.client.getGroupId()
version = deps.simpleArch.client.getVersion()

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
                api(deps.simpleArch.api.client)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(deps.kotlin.testCommon)
                implementation(deps.kotlin.testAnnotationCommon)
            }
        }

        val androidMain by getting {
            dependencies {
                api(deps.android.appCompat)
                api(deps.android.supportCompat)
                api(deps.android.core)
                api(deps.android.vectordrawable)

                api(deps.android.material)
                api(deps.android.work)
                deps.android.compose.forEach { api(it) }

                api(deps.android.anko)
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(deps.kotlin.testJunit)
                implementation(deps.junit)
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
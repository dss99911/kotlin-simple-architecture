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

apply(plugin = "org.jetbrains.kotlin.kapt")

apply(plugin = "kotlinx-serialization")

simpleArch {
    postfix = "simple"
    isInternal = true
}
apply(plugin = deps.simpleArch.api.gradle.toPlugInId())

group = deps.simpleArch.api.client.getGroupId()
version = deps.simpleArch.api.client.getVersion()

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
                api(deps.kotlin.serializationCore) //todo ktor-client-serialization doesn't support map<String, Any>. if it's supported, this can be removed.
                api(deps.ktor.clientSerialization) //todo ktor-client-serialization doesn't support map<String, Any>. if it's supported, this can be removed.
                api(deps.kotlin.reflect)


                api(deps.ktor.clientCore)

                api(deps.simpleArch.api.annotation)
            }
        }

        val jsMain by getting {
            dependencies {
                api(deps.ktor.clientJs)
            }
        }

        val jvmShareMain by creating {

        }

        val jvmMain by getting {
            dependsOn(jvmShareMain)
        }

        val androidMain by getting {
            dependsOn(jvmShareMain)
            dependencies {
                api(deps.ktor.clientAndroid)

            }
        }

        val iosMain by getting {
            dependencies {
                api(deps.ktor.clientIos)
            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
}

project.tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

android {
    compileSdkVersion(config.compileSdkVersion)
    buildToolsVersion(config.buildToolVersion)
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(config.targetSdkVersion)
    }
}

System.setProperty(// Enabling kotlin compiler plugin
    "kotlin.compiler.execution.strategy",
    "in-process"
)

publishMPP()
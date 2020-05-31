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
            }
        }
        //TODO HYUN [multi-platform2] : consider to change to clientMain. as front end also may be included to here
        val mobileMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
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
            dependsOn(jvmMain)

            dependencies {
                api(deps.kotlin.coroutineAndroid)
                api(deps.ktor.clientAndroid)

                api(deps.android.appCompat)
                api(deps.android.supportCompat)
                api(deps.android.core)
                api(deps.android.vectordrawable)

                api(deps.android.recyclerView)
                api(deps.android.material)
                api(deps.android.preference)
                api(deps.android.fragment)
                // Once https://issuetracker.google.com/127986458 is fixed this can be testImplementation
                api(deps.android.fragmentTesting)
                api(deps.android.viewPager)
                api(deps.android.work)
                api(deps.android.paging)
                api(deps.android.constraintlayout)

                deps.android.lifecycle.forEach { api(it) }
                deps.android.navigation.forEach { api(it) }

                //todo how to use koin on native? common can't use koin library. consider ktor use koin as well
                api(deps.koin.core)
                api(deps.koin.android)
                api(deps.koin.androidXScope)
                api(deps.koin.androidXViewModel)

                /*[START] Retrofit */
                //TODO HYUN [multi-platform2] : convert to ktor client
                api("com.squareup.retrofit2:retrofit:2.6.2")
                api("com.squareup.retrofit2:converter-gson:2.6.2")
                api("com.squareup.okhttp3:okhttp:4.3.0")
                api("com.squareup.okhttp3:logging-interceptor:4.3.0")
                /*[END] Retrofit */

                api(deps.android.picasso)
                api(deps.android.anko)

                api(deps.android.timber)

                //todo after upgrade kotlin from 1.3.61 to 1.3.71. there were the build error below
                //it's strange. let's try after moving to multimplatform module
                /** https://github.com/google/dagger/issues/95
                 * /Users/hyun.kim/AndroidstudioProjects/my/androidLibrary/sample/build/generated/source/kapt/freeDevDebug/androidx/databinding/library/baseAdapters/BR.java:5: error: cannot find symbol
                @Generated("Android Data Binding")
                ^
                symbol: class Generated
                 */
                api("org.glassfish:javax.annotation:10.0-b28")
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

    dataBinding {
        isEnabled = true
        isEnabledForTests = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.build {
    finalizedBy(tasks.publishToMavenLocal)
}
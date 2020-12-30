plugins {
    //android
    id("com.android.library")

    //common
    kotlin("multiplatform")
}
apply(plugin = "org.jetbrains.kotlin.kapt")
apply(plugin = deps.simpleApi.gradlePluginId)

kotlin {
    android()

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(deps.simpleApi.client)
            }
        }

        val jvmSharedMain by creating {
            dependencies {
                api(deps.ktor.clientGson)
                api(deps.ktor.clientLogging)
            }
        }

        val jvmMain by getting {
            dependsOn(jvmSharedMain)
        }

        val androidMain by getting {
            dependsOn(jvmSharedMain)
        }
    }
}

project.tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
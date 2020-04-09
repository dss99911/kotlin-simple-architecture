plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}

android {
    compileSdkVersion(config.compileSdkVersion)
    buildToolsVersion(config.buildToolVersion)
    defaultConfig {
        minSdkVersion(config.minSdkVersion)
        targetSdkVersion(config.targetSdkVersion)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    dataBinding {
        isEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

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

    apis(deps.android.lifecycle)
    apis(deps.android.navigation)
    apis(deps.android.room)
    apis(deps.android.coin)

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
    api(deps.simpleArch.jvm)
}

publish(false, true, "android", "Android")
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
    }

    dataBinding {
        isEnabled = true
        isEnabledForTests = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lintOptions {
        isAbortOnError = false
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


dependencies {
    implementation(deps.simpleArch.android)

    apis(deps.android.testCommon)

    //mockito
    //todo org.mockito:mockito-android is suggested to use but it's not working. so, dexmaker-mockito is used.
    api("com.linkedin.dexmaker:dexmaker-mockito:2.25.1")

    //duplicate class deifinition with dexmaker-mockito
    api("org.koin:koin-test:${versions.koin}") {
        exclude("org.mockito")
    }

    //espresso
    api("androidx.test.espresso:espresso-core:${versions.android.xEspresso}")
    api("androidx.test.espresso:espresso-contrib:${versions.android.xEspresso}")
    api("androidx.test.espresso:espresso-intents:${versions.android.xEspresso}")
}

publish(true, true, "android-androidtest", "Android AndroidTest")
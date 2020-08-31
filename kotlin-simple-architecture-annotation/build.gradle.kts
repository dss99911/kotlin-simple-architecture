plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

group = deps.simpleArch.annotation.getGroupId()
version = deps.simpleArch.annotation.getVersion()

kotlin {
    explicitApi()

    //if this multiplatform doesn't include any platform that your project is using, then your project won't recognize this library
    jvm()
    js().browser()

    ios()
    //todo is this required? try to remove
    android {
        publishLibraryVariants("release", "debug")
    }
}

android {
    compileSdkVersion(config.compileSdkVersion)
    buildToolsVersion(config.buildToolVersion)
    defaultConfig {
        minSdkVersion(config.minSdkVersion)
        targetSdkVersion(config.targetSdkVersion)

    }
}

publishMPP()
plugins {
    kotlin("multiplatform")
}

group = deps.simpleArch.api.annotation.getGroupId()
version = deps.simpleArch.api.annotation.getVersion()

kotlin {
    explicitApi()

    //if this multiplatform doesn't include any platform that your project is using, then your project won't recognize this library
    jvm()
    js()/*.browser()*/

    ios()

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }

}

publishMPP()
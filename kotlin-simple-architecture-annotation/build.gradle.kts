plugins {
    kotlin("multiplatform")
}

group = deps.simpleArch.annotation.getGroupId()
version = deps.simpleArch.annotation.getVersion()

kotlin {
    explicitApi()

    //if this multiplatform doesn't include any platform that your project is using, then your project won't recognize this library
    jvm()
    js()/*.browser()*/

    ios()
}

publishMPP()
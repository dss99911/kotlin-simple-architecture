//this is new way. https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block
plugins {
    kotlin("multiplatform") version versions.kotlin.version apply false
    id("kotlinx-serialization") version versions.kotlin.version apply false

    //for creating jar
    id("com.github.johnrengelman.shadow") version versions.shadow apply false
}

buildscript {
    repositories {
        mavenLocal()
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://dl.bintray.com/jetbrains/kotlin-native-dependencies")
        maven("https://dl.bintray.com/kotlin/kotlin-dev")

        google()
        jcenter()
    }

    //this is old way. https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application
    dependencies {
        //todo is it possible to omit these?
        classpath(deps.android.buildToolGradle)
        classpath(deps.kotlin.gradle)
        classpath(deps.android.navigationGradle)
        classpath(deps.shadowGradle)
        classpath(deps.bintrary.gradle)
        classpath(deps.simpleArch.pluginGradle)
        classpath(deps.sqldelight.gradle)
        classpath(deps.koin.gradle)
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/kotlin/ktor")
        maven("https://dl.bintray.com/sargunster/maven")
        maven("https://dl.bintray.com/kotlin/squash")
        maven("https://dl.bintray.com/kotlin/kotlin-dev")
        google()
        jcenter()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

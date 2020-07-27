
//this is new way. https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block
plugins {
    kotlin("multiplatform") version versions.kotlin.version apply false
    id("kotlinx-serialization") version versions.kotlin.version apply false
    id("kim.jeonghyeon.kotlin-simple-architecture-gradle-plugin") version versions.simpleArch apply false
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
        classpath(deps.shadowGradle)   //for creating jar
        classpath(deps.plugin.gradlePublish)
        classpath(deps.sqldelight.gradle)
        //todo check if possible to add on sample module only not here
        classpath("com.google.gms:google-services:4.3.3")
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

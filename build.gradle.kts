//this is new way. https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block
plugins {
    kotlin("multiplatform") version versions.kotlin.version apply false
    id("kotlinx-serialization") version versions.kotlin.version apply false

    //for creating jar
    id("com.github.johnrengelman.shadow") version versions.shadow apply false
}

buildscript {
    repositories {
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://dl.bintray.com/jetbrains/kotlin-native-dependencies")
        maven("https://dl.bintray.com/kotlin/kotlin-dev")

        google()
        jcenter()
    }

    //this is old way. https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application
    dependencies {
        classpath(deps.android.buildToolGradle)
        classpath(deps.android.navigationGradle)
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
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

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
        classpath(deps.android.buildToolGradle)
        classpath(deps.kotlin.gradle)
        classpath(deps.android.navigationGradle)
        classpath(deps.shadowGradle)
        classpath(deps.bintrary.gradle)
        classpath("kim.jeonghyeon:kotlin-simple-architecture-gradle-plugin:1.0.2")
//        classpath("com.squareup.sqldelight:gradle-plugin:1.3.0")
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

//configurations.all {
//    resolutionStrategy {
//        force("org.antlr:antlr4-runtime:4.5.3")
//        force("org.antlr:antlr4-tool:4.5.3")
//    }
//}
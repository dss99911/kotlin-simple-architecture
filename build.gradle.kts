
//this is new way. https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block
plugins {
    kotlin("multiplatform") version versions.kotlin.version apply false
}

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    //this is old way. https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application
    dependencies {
        //todo is it possible to omit these?
        classpath(deps.android.buildToolGradle)
        classpath(deps.kotlin.gradle)
        classpath(deps.shadowGradle)   //for creating jar
        classpath(deps.sqldelight.gradle)
        classpath(deps.kotlin.serializationGradle)
        classpath(deps.plugin.gradlePublish)//gradle plugin publishing
    }


}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}
//todo error, remove?
//tasks.register<Delete>("clean") {
//    delete(rootProject.buildDir)
//}
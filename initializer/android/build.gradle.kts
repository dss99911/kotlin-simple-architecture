buildscript {
    repositories {
        mavenLocal()
        google()
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath(deps.simpleArch.gradle)
        classpath(deps.android.buildToolGradle)
        classpath(deps.kotlin.gradle)
        classpath(deps.sqldelight.gradle)
        classpath(deps.kotlin.serializationGradle)
    }
}


allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
    }
}

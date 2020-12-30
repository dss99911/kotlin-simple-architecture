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
        classpath(deps.simpleApi.gradle)
        classpath(deps.android.buildToolGradle)
        classpath(deps.shadowGradle)//for creating jar of backend
        classpath(deps.kotlin.gradle)
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

System.setProperty(// Enabling kotlin compiler plugin
    "kotlin.compiler.execution.strategy",
    "in-process"
)

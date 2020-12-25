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
    }
}

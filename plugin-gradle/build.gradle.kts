plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    `java-gradle-plugin`
    id("kotlin-kapt")
    maven
}

group = deps.simpleArch.pluginGradle.getGroupId()
val archivesBaseName = deps.simpleArch.pluginGradle.getArtifactId()
version = deps.simpleArch.pluginGradle.getVersion()

//TODO HYUN [multi-platform2] : change to remote class path
tasks.install {
    repositories.withGroovyBuilder {
        "mavenInstaller" {
            "pom" {
                setProperty("artifactId", archivesBaseName)
            }
        }
    }
}

gradlePlugin {
    plugins {
        register("gradlePlugin") {
            id =
                archivesBaseName// users will do `apply plugin: "kotlin-simple-architecture-gradle-plugin"`
            implementationClass =
                "kim.jeonghyeon.simplearchitecture.plugin.MainGradlePlugin" // entry-point class
        }
    }
}

dependencies {
    implementation(project(":${deps.simpleArch.pluginShared.getArtifactId()}"))
    implementation(deps.plugin.gradleApi)
    implementation(deps.kotlin.gradle)
    implementation(deps.android.buildToolGradle)

    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)

}

tasks.build {
    dependsOn(":${deps.simpleArch.pluginApi.getArtifactId()}:build")
    dependsOn(":${deps.simpleArch.pluginApiNative.getArtifactId()}:build")

    finalizedBy(tasks.install)
}
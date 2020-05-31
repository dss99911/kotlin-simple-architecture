plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
    maven
}

group = deps.simpleArch.pluginApi.getGroupId()
val archivesBaseName = deps.simpleArch.pluginApi.getArtifactId()
version = deps.simpleArch.pluginApi.getVersion()

tasks.install {
    repositories.withGroovyBuilder {
        "mavenInstaller" {
            "pom" {
                setProperty("artifactId", archivesBaseName)
            }
        }
    }
}

dependencies {
    implementation(project(":gradle-plugin:${deps.simpleArch.pluginShared.getArtifactId()}"))
    compileOnly(deps.plugin.compilerEmbeddable)
    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)
}

//todo able to remove?
kapt {
    includeCompileClasspath = true
}

tasks.build {
    dependsOn(":gradle-plugin:${deps.simpleArch.pluginShared.getArtifactId()}:build")
    finalizedBy(tasks.install)
}
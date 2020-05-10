plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
    maven
}

group = "kim.jeonghyeon"
val archivesBaseName = "kotlin-simple-architecture-gradle-plugin-api"
version = "1.0.2"

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
//    implementation("kim.jeonghyeon:kotlin-simple-architecture-gradle-plugin-api-shared:1.0.2")
    implementation(project(":kotlin-simple-architecture-gradle-plugin-api-shared"))
    implementation(deps.kotlin.stdlibJdk8)
    implementation(deps.plugin.mpapt)
    compileOnly(deps.plugin.compilerEmbeddable)
    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)
}

//todo able to remove?
kapt {
    includeCompileClasspath = true
}
tasks.build {
    dependsOn(":kotlin-simple-architecture-gradle-plugin-api-shared:build")
    finalizedBy(tasks.install)
//    dependsOn(tasks.install)
}
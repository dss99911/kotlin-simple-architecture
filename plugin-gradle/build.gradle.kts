plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    `java-gradle-plugin`
    id("kotlin-kapt")
    maven
}

group = "kim.jeonghyeon"
val archivesBaseName = "kotlin-simple-architecture-gradle-plugin"
version = "1.0.2"

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

//publishing {
//    repositories {
//        mavenLocal()
//    }
//}

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
//gradlePlugin {
//    plugins {
//        create("simplePlugin") {
//            id = archivesBaseName// users will do `apply plugin: "kotlin-simple-architecture-gradle-plugin"`
//            implementationClass = "kim.jeonghyeon.simplearchitecture.plugin.MainGradlePlugin" // entry-point class
//        }
//    }
//}

dependencies {
    implementation(deps.kotlin.stdlibJdk8)
    implementation(deps.plugin.gradleApi)
    implementation(deps.kotlin.gradle)
    implementation(deps.android.buildToolGradle)

    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)

}

tasks.build {
//    dependsOn(":kotlin-simple-architecture-gradle-plugin-api-shared:build")
    dependsOn(":plugin-api:build")
    dependsOn(":kotlin-simple-architecture-gradle-plugin-api-native:build")

    finalizedBy(tasks.install)
}
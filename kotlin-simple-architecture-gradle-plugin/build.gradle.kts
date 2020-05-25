plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    `java-gradle-plugin`
    id("kotlin-kapt")
    maven
    id("org.jetbrains.intellij") version "0.4.21"
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

val GENERATED_SOURCE_PATH = "build/generated/source/simpleArch"

sourceSets {
    getByName("main").java.srcDir(GENERATED_SOURCE_PATH)
}

tasks.create("pluginConfig") {

    val outputDir = file(GENERATED_SOURCE_PATH)
    outputs.dir(outputDir)

    doLast {
        val configFile = file("$outputDir/kim/jeonghyeon/simplearchitecture/plugin/PluginConfig.kt")
        configFile.parentFile.mkdirs()
        configFile.writeText("""
            package kim.jeonghyeon.simplearchitecture.plugin
            
            val DEPENDENCY_SIMPLE_ARCHITECTURE = "${deps.simpleArch.common}"
            val DEPENDENCY_SIMPLE_ARCHITECTURE_JVM = "${deps.simpleArch.jvm}"
            val DEPENDENCY_SIMPLE_ARCHITECTURE_PLUGIN_API = "${deps.simpleArch.pluginApi}"
            val DEPENDENCY_SIMPLE_ARCHITECTURE_PLUGIN_API_NATIVE = "${deps.simpleArch.pluginApiNative}"
        """.trimIndent())
    }
}

tasks.getByName("compileKotlin").dependsOn("pluginConfig")
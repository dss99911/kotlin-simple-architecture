plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    `java-gradle-plugin`
    id("kotlin-kapt")
    maven
    id("com.gradle.plugin-publish") version "0.12.0"
}

group = deps.simpleArch.pluginGradle.getGroupId()
val archivesBaseName = deps.simpleArch.pluginGradle.getArtifactId()
version = deps.simpleArch.pluginGradle.getVersion()


//region gradle plugin publish
pluginBundle {
    website = "https://github.com/dss99911/kotlin-simple-architecture"
    vcsUrl = "https://github.com/dss99911/kotlin-simple-architecture.git"
    tags = listOf("kotlin", "multiplatform", "architecture", "kotlincompilerplugin")
}

gradlePlugin {
    plugins {
        create("mainPlugin") {//name is for unique in all plugins
            id = "$group.$archivesBaseName"
            displayName = "Kotlin Simple Architecture Plugin"
            description = "Generate code for Simple Api and Simple DB, etc"
            implementationClass = "kim.jeonghyeon.simplearchitecture.plugin.MainGradlePlugin" // entry-point class
        }
    }
}

dependencies {
    implementation(project(":gradle-plugin:${deps.simpleArch.pluginShared.getArtifactId()}"))
    implementation(deps.kotlin.gradle)
    implementation(deps.android.buildToolGradle)

    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)

}

//build all plugins by one time
tasks.build {
    dependsOn(":gradle-plugin:${deps.simpleArch.pluginApi.getArtifactId()}:build")
    dependsOn(":gradle-plugin:${deps.simpleArch.pluginApiNative.getArtifactId()}:build")

    finalizedBy(tasks.install)
}


//region plugin generated config

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
            val VERSION_COMPOSE = "${versions.android.compose}"
        """.trimIndent()
        )
    }
}

tasks.getByName("compileKotlin").dependsOn("pluginConfig")

//endregion plugin generated config

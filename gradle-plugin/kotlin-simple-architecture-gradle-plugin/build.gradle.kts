plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm")
    `java-gradle-plugin`
    id("kotlin-kapt")
    `maven-publish`
    maven
}

group = deps.simpleArch.pluginGradle.getGroupId()
version = deps.simpleArch.pluginGradle.getVersion()

dependencies {
    implementation(project(":gradle-plugin:${deps.simpleArch.pluginShared.getArtifactId()}"))
    implementation(deps.kotlin.gradle)
    implementation(deps.android.buildToolGradle)

    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)

}

//build all plugins by one time
//tasks.build {
////    dependsOn(":gradle-plugin:${deps.simpleArch.pluginApi.getArtifactId()}:build")
////    dependsOn(":gradle-plugin:${deps.simpleArch.pluginApiNative.getArtifactId()}:build")
//    finalizedBy(tasks.install)
//}


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

publishGradlePlugin()

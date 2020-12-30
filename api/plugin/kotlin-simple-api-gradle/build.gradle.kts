plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm")
    `java-gradle-plugin`
    id("kotlin-kapt")
    `maven-publish`//for local publish
}

group = deps.simpleArch.api.gradle.getGroupId()
version = deps.simpleArch.api.gradle.getVersion()

dependencies {
//    implementation(project(":gradle-plugin:${deps.simpleArch.api.gradleServiceShared.getArtifactId()}"))
    api(deps.simpleArch.api.gradleServiceShared)
    api(deps.kotlin.gradle)
    api(deps.android.buildToolGradle)
    api(deps.sqldelight.gradle)

    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)

}


//region plugin generated config
//todo how to move to other kts file? sourceSets is not recognized
val GENERATED_SOURCE_PATH = "build/generated/source/simpleArch"

sourceSets {
    getByName("main").java.srcDir(GENERATED_SOURCE_PATH)
}

tasks.create("pluginConfigApi") {

    val outputDir = file(GENERATED_SOURCE_PATH)
    outputs.dir(outputDir)

    doLast {
        val configFile = file("$outputDir/kim/jeonghyeon/simplearchitecture/plugin/PluginConfigApi.kt")
        configFile.parentFile.mkdirs()
        configFile.writeText("""
            package kim.jeonghyeon.simplearchitecture.plugin

            val DEPENDENCY_SIMPLE_ARCHITECTURE_PLUGIN_API = "${deps.simpleArch.api.gradleService}"
            val DEPENDENCY_SIMPLE_ARCHITECTURE_PLUGIN_API_NATIVE = "${deps.simpleArch.api.gradleServiceNative}"
        """.trimIndent()
        )
    }
}

tasks.getByName("compileKotlin").dependsOn("pluginConfigApi")

//endregion plugin generated config

publishGradlePlugin()
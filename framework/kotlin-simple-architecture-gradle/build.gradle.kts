plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm")
    `java-gradle-plugin`
    id("kotlin-kapt")
    `maven-publish`//for local publish
}

group = deps.simpleArch.gradle.getGroupId()
version = deps.simpleArch.gradle.getVersion()

dependencies {
    implementation(deps.simpleArch.api.gradle)
    implementation(deps.sqldelight.gradle)
    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)

}


//region plugin generated config
//todo how to move to other kts file? sourceSets is not recognized
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

            val DEPENDENCY_SIMPLE_ARCHITECTURE = "${deps.simpleArch.client}"
            val VERSION_COMPOSE = "${versions.android.compose}"
            val VERSION_KOTLIN = "${versions.kotlin.version}"
        """.trimIndent()
        )
    }
}

tasks.getByName("compileKotlin").dependsOn("pluginConfig")

//endregion plugin generated config

publishGradlePlugin()
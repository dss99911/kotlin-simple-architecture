plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
}

group = deps.simpleArch.pluginApi.getGroupId()
version = deps.simpleArch.pluginApi.getVersion()

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
//    dependsOn(":gradle-plugin:${deps.simpleArch.pluginShared.getArtifactId()}:build")
}

publishJvm()
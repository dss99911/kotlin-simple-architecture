plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
}

group = deps.simpleArch.api.gradleServiceNative.getGroupId()
version = deps.simpleArch.api.gradleServiceNative.getVersion()

dependencies {
//    implementation(project(":gradle-plugin:${deps.simpleArch.api.gradleServiceShared.getArtifactId()}"))
    implementation(deps.simpleArch.api.gradleServiceShared)
    compileOnly(deps.plugin.compiler)//for native
    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)
}

publishShadowJar()

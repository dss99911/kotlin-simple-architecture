plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
}

group = deps.simpleArch.pluginApiNative.getGroupId()
version = deps.simpleArch.pluginApiNative.getVersion()

dependencies {
//    implementation(project(":gradle-plugin:${deps.simpleArch.pluginShared.getArtifactId()}"))
    implementation(deps.simpleArch.pluginShared)
    compileOnly(deps.plugin.compiler)//for native
    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)
}


//todo what is this for?
kapt {
    includeCompileClasspath = true
}

publishShadowJar()

plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
}



//the reason that the module name is same with dependency's name
//- when build plugin-api, if the version of this dependency doesn't exist, gradle build error occurs.
//- so, plugin-api use implementation(project()), so that no error.
//- when plugin-api is used, plugin-api searches the dependency of "$group:$moduleName:$version"
//- that's why module name and dependency's name are same
group = deps.simpleArch.api.gradleServiceShared.getGroupId()
version = deps.simpleArch.api.gradleServiceShared.getVersion()

dependencies {
    api(deps.gson)
    api(deps.plugin.gradleApi)

    compileOnly(deps.plugin.compilerEmbeddable)
    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)
    api(deps.simpleArch.api.annotation)
}

publishJvm()
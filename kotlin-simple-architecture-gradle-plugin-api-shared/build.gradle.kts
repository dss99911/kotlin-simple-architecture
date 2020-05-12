plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
    maven
}



//the reason that the module name is same with dependency's name
//- when build plugin-api, if the version of this dependency doesn't exist, gradle build error occurs.
//- so, plugin-api use implementation(project()), so that no error.
//- when plugin-api is used, plugin-api searches the dependency of "$group:$moduleName:$version"
//- that's why module name and dependency's name are same
group = deps.simpleArch.pluginShared.getGroupId()
val archivesBaseName = deps.simpleArch.pluginShared.getArtifactId()
version = deps.simpleArch.pluginShared.getVersion()

tasks.install {
    repositories.withGroovyBuilder {
        "mavenInstaller" {
            "pom" {
                setProperty("artifactId", archivesBaseName)
            }
        }
    }
}

dependencies {
    api(deps.kotlin.stdlibJdk8)
    api(deps.gson)
    implementation(deps.plugin.poet)
    compileOnly(deps.plugin.compilerEmbeddable)
    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)
}

//todo able to remove?
kapt {
    includeCompileClasspath = true
}

tasks.build {
    finalizedBy(tasks.install)
}
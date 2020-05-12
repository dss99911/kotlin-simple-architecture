plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
    id("com.github.johnrengelman.shadow")
    maven
}

group = deps.simpleArch.pluginApiNative.getGroupId()
val archivesBaseName = deps.simpleArch.pluginApiNative.getArtifactId()
val ver = deps.simpleArch.pluginApiNative.getVersion().also {
    version = it
}

dependencies {
    implementation(project(":${deps.simpleArch.pluginShared.getArtifactId()}"))
    compileOnly(deps.plugin.compiler)//for native
    compileOnly(deps.plugin.auto)
    kapt(deps.plugin.auto)
}


tasks.getByName<Upload>("uploadArchives") {
    repositories.withGroovyBuilder {
        "mavenDeployer" {
            "repository"("url" to uri("${System.getProperty("user.home")}/.m2/repository"))
        }
    }
}

tasks.create<Jar>("fatJar") {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to "Gradle Jar File Example",
                "Implementation-Version" to ver,
                "Main-Class" to "kim.jeonghyeon.simplearchitecture.plugin.NativeApiComponentRegistrar"
            )
        )
    }
    archiveBaseName.value(archivesBaseName)
    archiveVersion.value(ver)

    from(configurations.compileClasspath.map { config ->
        config.map {
            if (it.isDirectory) it else zipTree(
                it
            )
        }
    })
}

//todo is this required? what's the purpose?
tasks.shadowJar {
    manifest {
        from(project.tasks.getByName<Jar>("fatJar").manifest)
    }
    archiveBaseName.value(archivesBaseName)
    archiveVersion.value(ver)
    classifier = null
}

publishing {
    publications {
        register<MavenPublication>("shadow") {
            project.shadow.component(this)
        }
    }
    repositories {
        maven {
            url = uri("${System.getProperty("user.home")}/.m2/repository")
        }
    }
}

//todo able to remove?
kapt {
    includeCompileClasspath = true
}

tasks.install {
    dependsOn(tasks.shadowJar)

}

tasks.build {
    dependsOn(":${deps.simpleArch.pluginShared.getArtifactId()}:build")
    dependsOn(tasks.shadowJar)
    finalizedBy(tasks.publishToMavenLocal)
}
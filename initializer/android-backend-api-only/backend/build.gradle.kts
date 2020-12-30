plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

apply(plugin = deps.simpleApi.gradlePluginId)

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

project.tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

dependencies {
    implementation(project(":base"))
    implementation(deps.simpleApi.backend)
    implementation(deps.ktor.gson)
    implementation(deps.ktor.serverNetty)
}
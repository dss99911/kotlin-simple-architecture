import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}
apply(plugin = "kotlinx-serialization")

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
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

project.tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

dependencies {
    implementation(project(":sample-base"))
    implementation(deps.simpleArch.backend)
}
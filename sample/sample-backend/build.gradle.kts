import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

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
    implementation(project(":sample:sample-base"))
    if (config.buildByProject) {
        implementation(project(":framework:${deps.simpleArch.backend.getArtifactId()}"))
    } else {
        implementation(deps.simpleArch.backend)
    }

}
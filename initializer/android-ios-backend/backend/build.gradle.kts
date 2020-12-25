plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

apply(plugin = "kim.jeonghyeon.kotlin-simple-architecture-gradle-plugin")

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

dependencies {
    implementation(project(":base"))
}
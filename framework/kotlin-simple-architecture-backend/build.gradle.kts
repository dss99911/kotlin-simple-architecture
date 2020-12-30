plugins {
    kotlin("multiplatform")
}

group = deps.simpleArch.backend.getGroupId()
version = deps.simpleArch.backend.getVersion()

val buildByLibrary: Boolean? by project

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {

                if (buildByLibrary == true) {
                    api(deps.simpleArch.api.backend)
                } else {
                    api(project(":api:library:${deps.simpleArch.api.backend.getArtifactId()}"))
                }


                if (buildByLibrary == true) {
                    api(deps.simpleArch.client)
                } else {
                    api(project(":framework:${deps.simpleArch.client.getArtifactId()}"))
                }


                api(deps.ktor.authJwt)
                api(deps.ktor.clientEngineApache)
                api(deps.ktor.serverSessions)
                api(deps.ktor.serverNetty)

                api(deps.logback)

            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
}

project.tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishMPP()
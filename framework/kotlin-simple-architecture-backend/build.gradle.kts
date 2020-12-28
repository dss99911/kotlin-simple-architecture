plugins {
    kotlin("multiplatform")
}

group = deps.simpleArch.backend.getGroupId()
version = deps.simpleArch.backend.getVersion()

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {

                if (config.buildByProject) {
                    api(project(":api:library:${deps.simpleArch.api.backend.getArtifactId()}"))
                } else {
                    api(deps.simpleArch.api.backend)
                }


                if (config.buildByProject) {
                    api(project(":framework:${deps.simpleArch.client.getArtifactId()}"))
                } else {
                    api(deps.simpleArch.client)
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
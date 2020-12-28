plugins {
    kotlin("multiplatform")
}

group = deps.simpleArch.api.backend.getGroupId()
version = deps.simpleArch.api.backend.getVersion()

kotlin {

    jvm()


    sourceSets {
        val commonMain by getting {
            dependencies {
                if (config.buildByProject) {
                    api(project(":api:library:${deps.simpleArch.api.client.getArtifactId()}"))
                } else {
                    api(deps.simpleArch.api.client)
                }
            }
        }

        val jvmMain by getting {

            dependencies {
                api(deps.ktor.core)
                api(deps.ktor.auth)
                api(deps.ktor.serialization)
            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
}

publishMPP()
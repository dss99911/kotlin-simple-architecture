plugins {
    kotlin("multiplatform")
}
apply(plugin = "kotlinx-serialization")

val buildByLibrary: String? by project

group = deps.simpleArch.api.backend.getGroupId()
version = deps.simpleArch.api.backend.getVersion()

kotlin {

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                if (buildByLibrary == "true") {
                    api(deps.simpleArch.api.client)
                } else {
                    api(project(":api:library:${deps.simpleArch.api.client.getArtifactId()}"))
                }
            }
        }

        val jvmMain by getting {

            dependencies {
                api(deps.ktor.core)
                api(deps.ktor.auth)
                api(deps.ktor.serialization)
                api(deps.gson)
            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
}

publishMPP()
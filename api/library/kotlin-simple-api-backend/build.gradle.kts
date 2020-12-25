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
                api(deps.simpleArch.api.client)
            }
        }

        val jvmMain by getting {

            dependencies {
                api(deps.ktor.core)
                api(deps.ktor.auth)
                api(deps.ktor.authJwt)
                api(deps.ktor.gson)
                api(deps.ktor.serverNetty)
                api(deps.ktor.serialization)
                api(deps.ktor.clientEngineApache)
                api(deps.ktor.serverSessions)

                api(deps.logback)

                api(deps.kotlin.reflect)
            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
}

publishMPP()
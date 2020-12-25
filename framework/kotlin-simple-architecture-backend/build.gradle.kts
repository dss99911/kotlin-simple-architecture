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
                api(deps.simpleArch.api.backend)
                api(deps.simpleArch.client)
            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
}

publishMPP()
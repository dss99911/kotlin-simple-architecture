//on gradle-6.3-all, can not use extra properties
val includeSample = true

//todo : is there way to remove pluginManagement below?
// this is the replace of
// `classpath(kotlin("serialization", version = kotlinVersion))`
// Try to change to the way below
// plugins {
//    kotlin("plugin.serialization") version "1.3.70"
// }
pluginManagement {
    resolutionStrategy {
        eachPlugin {
            val plugin = requested.id.id
            when (plugin) {
                "kotlinx-serialization" -> useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }
    }
}

enableFeaturePreview("GRADLE_METADATA")

include("kotlin-simple-architecture")
include("gradle-plugin:kotlin-simple-architecture-gradle-plugin")
include("gradle-plugin:kotlin-simple-architecture-gradle-plugin-api")
include("gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-shared")
include("gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-native")


if (includeSample) {
    include("sample")
}

//todo remove after bug fixed
include(":sampleandroid")

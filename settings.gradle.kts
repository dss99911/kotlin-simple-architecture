//on gradle-6.3-all, can not use extra properties
val includeBackend = true
val includeAndroid = true

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

include("common")
include("kotlinlibrary")
include("plugin-gradle")
include("plugin-api")
include("kotlin-simple-architecture-gradle-plugin-api-shared")
include("kotlin-simple-architecture-gradle-plugin-api-native")
include("sample-plugin")

if (includeBackend) {
    include("backend")
}


if (includeAndroid) {
    include("library")
    include("androidtesting", "testing")
    include ("sample"/*, "kotlin-sample", "sample-testing-codelab", "sample-sunflower"*/)
}


//on gradle-6.3-all, can not use extra properties
val includeSample = true

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
include("kotlin-simple-architecture-gradle-plugin")
include("kotlin-simple-architecture-gradle-plugin-api")
include("kotlin-simple-architecture-gradle-plugin-api-shared")
include("kotlin-simple-architecture-gradle-plugin-api-native")


if (includeSample) {
//    include("androidtesting", "testing")
//    include ("sample"/*, "kotlin-sample", "sample-testing-codelab", "sample-sunflower"*/)
    include("sample")
}


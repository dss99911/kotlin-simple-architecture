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

if (includeBackend) {
    include("backend")
}


if (includeAndroid) {
    include("library")//TODO HYUN [master] : separate to base and library. and library with repository.
    include("androidtesting", "testing") //TODO HYUN [master] : make repo
    include ("sample"/*, "kotlin-sample", "sample-testing-codelab", "sample-sunflower"*/)
}

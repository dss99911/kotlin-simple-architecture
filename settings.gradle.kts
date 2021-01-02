val includingModuleName: String? by settings

if (includingModuleName != null) {
    if (includingModuleName!!.contains(":")) {
        include(includingModuleName!!.substringBefore(":"))
    }
    include(includingModuleName)
} else {

    include("api:plugin:kotlin-simple-api-gradle-service-shared")
    include("api:plugin:kotlin-simple-api-gradle-service")
    include("api:plugin:kotlin-simple-api-gradle-service-native")
    include("api:plugin:kotlin-simple-api-gradle")

    include("api:library:kotlin-simple-api-annotation")
    include("api:library:kotlin-simple-api-client")
    include("api:library:kotlin-simple-api-backend")
    include("framework:kotlin-simple-architecture-gradle")
    include("framework:kotlin-simple-architecture-client")
    include("framework:kotlin-simple-architecture-backend")

    include("temp-test")
}
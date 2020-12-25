val includingModuleName: String? by settings

if (includingModuleName != null) {
    if (includingModuleName!!.contains(":")) {
        include(includingModuleName!!.substringBefore(":"))
    }
    include(includingModuleName)
} else {
    include("kotlin-simple-architecture-annotation")
//    include("gradle-plugin")
//    include("gradle-plugin:kotlin-simple-architecture-gradle-plugin")
    include("api:library:kotlin-simple-api-client")
    include("api:library:kotlin-simple-api-backend")
    include("api:plugin:kotlin-simple-api-gradle")
    include("api:plugin:kotlin-simple-api-gradle-service")
    include("api:plugin:kotlin-simple-api-gradle-service-native")
    include("api:plugin:kotlin-simple-api-gradle-service-shared")
//    include("kotlin-simple-architecture")
//    include("sample")
//    include("sample:sample-base")
//    include("sample:sample-android")
//    include("sample:sample-backend")
}
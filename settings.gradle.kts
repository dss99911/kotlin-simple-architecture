val includingModuleName: String? by settings

if (includingModuleName != null) {
    if (includingModuleName!!.contains(":")) {
        include(includingModuleName!!.substringBefore(":"))
    }
    include(includingModuleName)
} else {
    include("kotlin-simple-architecture-annotation")
    include("gradle-plugin")
    include("gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-shared")
    include("gradle-plugin:kotlin-simple-architecture-gradle-plugin-api")
    include("gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-native")
    include("gradle-plugin:kotlin-simple-architecture-gradle-plugin")
    include("kotlin-simple-architecture")
    include("sample")
    include("sample:sample-base")
    include("sample:sample-android")
    include("sample:sample-backend")
}
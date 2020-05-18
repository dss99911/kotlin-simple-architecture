package kim.jeonghyeon.simplearchitecture.plugin

const val NATIVE_TARGET_NAME = "native"

data class SourceSetOption(val name: String, val sourcePathSet: Set<String>)

fun generatedSourceSetPath(buildPath: String, sourceSetName: String): String =
    "${generatedPath(buildPath)}/source/simpleapi/$sourceSetName"

fun generatedPath(buildPath: String): String =
    "${buildPath}/generated"
package kim.jeonghyeon.simplearchitecture.plugin

const val NATIVE_TARGET_NAME = "native"

data class SourceSetOption(val name: String, val sourcePathSet: Set<String>)

fun generatedFilePath(buildPath: String, sourceSetName: String): String =
    "${buildPath}/generated/source/simpleapi/$sourceSetName"
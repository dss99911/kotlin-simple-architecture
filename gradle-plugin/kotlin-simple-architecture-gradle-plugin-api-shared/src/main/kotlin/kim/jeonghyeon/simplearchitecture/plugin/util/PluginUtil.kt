package kim.jeonghyeon.simplearchitecture.plugin.util

fun generatedSourceSetPath(buildPath: String, sourceSetName: String): String =
    "${generatedPath(buildPath)}/source/simpleArch/$sourceSetName"

fun generatedPath(buildPath: String): String =
    "${buildPath}/generated"
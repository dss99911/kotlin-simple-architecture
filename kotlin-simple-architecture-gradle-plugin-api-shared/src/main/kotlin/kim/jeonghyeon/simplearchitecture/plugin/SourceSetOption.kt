package kim.jeonghyeon.simplearchitecture.plugin

/**
 * todo is there secure way for this?
 *  consider [KotlinSourceSet.dependsOn] property. and find most-top source set.
 */
const val SOURCE_SET_NAME_COMMON = "commonMain"

data class SourceSetOption(val name: String, val sourcePathSet: Set<String>) {

    fun isCommon() = name == SOURCE_SET_NAME_COMMON
}

fun generatedSourceSetPath(buildPath: String, sourceSetName: String): String =
    "${generatedPath(buildPath)}/source/simpleapi/$sourceSetName"

fun generatedPath(buildPath: String): String =
    "${buildPath}/generated"
package kim.jeonghyeon.simplearchitecture.plugin.model

/**
 * todo is there secure way for this?
 *  - there is possibility that common target name is different
 *  - there is possibility that commonMain can exist on not multiplatfrom
 *  consider [KotlinSourceSet.dependsOn] property. and find most-top source set.
 *  or check if it's multiplatform.
 */
const val SOURCE_SET_NAME_COMMON = "commonMain"

data class SourceSetOption(val name: String, val sourcePathSet: Set<String>) {

    fun isCommon() = name == SOURCE_SET_NAME_COMMON
}

fun generatedSourceSetPath(buildPath: String, sourceSetName: String): String =
    "${generatedPath(buildPath)}/source/simpleArch/$sourceSetName"

fun generatedPath(buildPath: String): String =
    "${buildPath}/generated"
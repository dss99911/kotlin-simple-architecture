package kim.jeonghyeon.simplearchitecture.plugin.model

import kim.jeonghyeon.simplearchitecture.plugin.util.generatedSourceSetPath
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

/**
 * todo is there secure way for this?
 *  - there is possibility that common target name is different
 *  - there is possibility that commonMain can exist on not multiplatfrom
 *  consider [KotlinSourceSet.dependsOn] property. and find most-top source set.
 *  or check if it's multiplatform.
 */
const val SOURCE_SET_NAME_COMMON = "commonMain"

data class PluginOptions(
    val platformType: KotlinPlatformType,
    val isMultiplatform: Boolean,
    val buildPath: String,
    val compileTargetVariantsName: String,
    val postFix: String//HttpClient.create(), db() prefix.if this is "simple" result will be HttpClient.createSimple(), dbSimple(). as the generated code only takes care of each module's source code. each module should use different prefix.
) {
    fun getGeneratedTargetVariantsPath(): String =
        generatedSourceSetPath(
            buildPath,
            compileTargetVariantsName
        )
}